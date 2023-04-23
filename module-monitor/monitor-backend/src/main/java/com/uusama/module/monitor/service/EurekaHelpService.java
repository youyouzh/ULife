package com.uusama.module.monitor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.uusama.common.util.CollUtil;
import com.uusama.module.monitor.mapper.DeployAgentMapper;
import com.uusama.module.monitor.mapper.DeployProjectMapper;
import com.uusama.module.monitor.mapper.DeployTaskMapper;
import com.uusama.module.monitor.domain.DeployState;
import com.uusama.module.monitor.entity.DeployAgentDO;
import com.uusama.module.monitor.entity.DeployProjectDO;
import com.uusama.module.monitor.entity.DeployTaskDO;
import com.netflix.appinfo.InstanceInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhaohai
 * eureka辅助服务，检查服务注册情况等
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EurekaHelpService {
    private static final String MONITOR_AGENT_SERVICE_ID = "monitor-agent";
    private final DiscoveryClient discoveryClient;
    private final DeployAgentMapper deployAgentDao;
    private final DeployTaskMapper deployTaskDao;
    private final DeployProjectMapper deployProjectDao;

    public List<ServiceInstance> getServiceInstance(String serviceCode) {
        return discoveryClient.getInstances(serviceCode);
    }

    public boolean existUpService(String serviceCode, String ip) {
        return discoveryClient.getInstances(serviceCode).stream()
            .map(v -> ((EurekaServiceInstance) v).getInstanceInfo())
            .filter(v -> ip.equals(v.getIPAddr()))
            .anyMatch(v -> v.getStatus() == InstanceInfo.InstanceStatus.UP);
    }

    /**
     * 检查Agent活动情况
     */
    @Scheduled(fixedDelay = 5000)
    public void checkAgentState() {
        Map<String, InstanceInfo> instanceMap = getServiceInstance(MONITOR_AGENT_SERVICE_ID).stream()
            .map(v -> ((EurekaServiceInstance) v).getInstanceInfo())
            .collect(Collectors.toMap(InstanceInfo::getIPAddr, v -> v));
        List<DeployAgentDO> deployAgents = deployAgentDao.selectList(Wrappers.emptyWrapper());

        // 依次更新已有的agent状态信息
        for (DeployAgentDO deployAgent : deployAgents) {
            InstanceInfo instanceInfo = instanceMap.get(deployAgent.getIp());
            if (Objects.isNull(instanceInfo)) {
                deployAgent.setEurekaState(InstanceInfo.InstanceStatus.OUT_OF_SERVICE);
            } else {
                deployAgent.setEurekaState(instanceInfo.getStatus());
            }
            deployAgentDao.updateById(deployAgent);
        }

        // 处理自动注册到Eureka，但是未创建相应DeployAgent记录的情况
        List<String> recordAgentIps = CollUtil.convertList(deployAgents, DeployAgentDO::getIp);
        instanceMap.values().stream()
            .filter(v -> !recordAgentIps.contains(v.getIPAddr()))
            .map(v -> DeployAgentDO.createWithIp(v.getIPAddr()))
            .forEach(deployAgentDao::insert);
    }

    /**
     * 检查当前部署中的服务是否已经注册到Eureka
     */
    @Scheduled(fixedDelay = 5000)
    public void checkDeployingTask() {
        List<DeployTaskDO> deployTasks = deployTaskDao.selectList(new LambdaQueryWrapper<DeployTaskDO>()
            .eq(true, DeployTaskDO::getDeployState, DeployState.DEPLOYING));
        if (CollectionUtils.isEmpty(deployTasks)) {
            return;
        }

        Map<Integer, DeployProjectDO> projectMap = deployProjectDao.selectMap(deployTasks);
        Map<Integer, DeployAgentDO> agentMap = deployAgentDao.selectMap(deployTasks);
        for (DeployTaskDO deployTask : deployTasks) {
            // 检查Eureka上服务注册状态，如果注册成功，则标记为部署完成
            DeployProjectDO deployProject = projectMap.get(deployTask.getProjectId());
            if (agentMap.containsKey(deployTask.getAgentId()) && existUpService(deployProject.getCode(), agentMap.get(deployTask.getAgentId()).getIp())) {
                deployTask.setDeployState(DeployState.SUCCESS);
                deployTask.setRemark("服务正常启动并成功注册到Eureka");
                deployTaskDao.updateById(deployTask);
            }
        }
    }
}
