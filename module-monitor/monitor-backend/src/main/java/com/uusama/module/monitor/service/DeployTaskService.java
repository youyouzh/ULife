package com.uusama.module.monitor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uusama.common.util.CollUtil;
import com.uusama.common.util.DateTimeUtil;
import com.uusama.framework.api.pojo.CommonResult;
import com.uusama.module.monitor.constant.AgentConstants;
import com.uusama.module.monitor.domain.AgentDeployTaskInfo;
import com.uusama.module.monitor.entity.DeployAgentDO;
import com.uusama.module.monitor.entity.DeployProjectDO;
import com.uusama.module.monitor.entity.DeployProjectVersionDO;
import com.uusama.module.monitor.entity.DeployTaskDO;
import com.uusama.module.monitor.entity.SysFileDO;
import com.uusama.module.monitor.mapper.DeployAgentMapper;
import com.uusama.module.monitor.mapper.DeployProjectMapper;
import com.uusama.module.monitor.mapper.DeployProjectVersionMapper;
import com.uusama.module.monitor.mapper.DeployTaskMapper;
import com.uusama.module.monitor.mapper.SysFileMapper;
import com.uusama.module.monitor.pojo.AmisCrudData;
import com.uusama.module.monitor.pojo.DeployTaskInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhaohai
 * 部署任务相关service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeployTaskService {
    private final DeployTaskMapper deployTaskDao;
    private final DeployProjectMapper deployProjectDao;
    private final DeployAgentMapper deployAgentDao;
    private final DeployProjectVersionMapper deployProjectVersionDao;
    private final SysFileMapper sysFileDao;

    public AmisCrudData<DeployTaskInfo> queryTasks(Integer projectId, Integer agentId, Integer batchTaskId, Page<DeployTaskDO> pageWrapper) {
        LambdaQueryWrapper<DeployTaskDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(projectId), DeployTaskDO::getProjectId, projectId)
            .eq(Objects.nonNull(agentId), DeployTaskDO::getAgentId, agentId)
            .eq(Objects.nonNull(batchTaskId), DeployTaskDO::getBatchTaskId, batchTaskId)
            .orderByDesc(DeployTaskDO::getId);

        // 如果查询结果为空则直接返回，避免下面填充数据查询
        Page<DeployTaskDO> taskPage = deployTaskDao.selectPage(pageWrapper, queryWrapper);
        if (taskPage.getRecords().isEmpty()) {
            log.info("query queryTasks empty. projectId: {}, agentId: {}", projectId, agentId);
            return AmisCrudData.empty();
        }

        // 填充agent和project信息
        Map<Integer, DeployProjectDO> projectMap = deployProjectDao.selectMap(taskPage.getRecords());
        Map<Integer, DeployProjectVersionDO> versionMap = deployProjectVersionDao.selectMap(taskPage.getRecords());
        Map<Integer, DeployAgentDO> agentMap = deployAgentDao.selectMap(taskPage.getRecords());

        return AmisCrudData.of(CollUtil.convertList(taskPage.getRecords(), v -> DeployTaskInfo
            .of(v, projectMap.get(v.getProjectId()), versionMap.get(v.getProjectVersionId()),
                agentMap.get(v.getAgentId()))), taskPage.getTotal());
    }

    public Optional<DeployTaskDO> createDeployTask(int projectId, int projectVersionId, int agentId, LocalDateTime deployStartTime) {
        // 检查当前项目是否有部署任务在执行中
        if (deployTaskDao.existDeployingTask(projectId, agentId)) {
            return Optional.empty();
        }

        // 创建部署任务
        DeployTaskDO deployTask = DeployTaskDO.builder()
            .projectId(projectId)
            .projectVersionId(projectVersionId)
            .agentId(agentId)
            .build();
        deployTask.initDeployStateAndTime(deployStartTime);
        deployTaskDao.insert(deployTask);
        return Optional.of(deployTask);
    }

    /**
     * 定时检查部署任务，对于到部署时间的任务自动执行
     */
    @Scheduled(fixedDelay = 60000)
    public void autoCheckDeployTask() {
        List<DeployTaskDO> deployTasks = deployTaskDao.selectRecentTasks();
        for (DeployTaskDO deployTask : deployTasks) {
            // 超时的任务自动设置为失败
            if (deployTask.isDeployingOvertime()) {
                log.warn("deployTask is overtime. taskId: {}", deployTask.getId());
                deployTask.failRemark("部署超时");
                deployTaskDao.updateById(deployTask);
            }

            // 到达部署时间的任务自动部署
            if (deployTask.attachAppointmentDeployTime()) {
                this.runDeployTask(deployTask);
            }
        }
    }

    /**
     * 执行部署，并将执行结果更新到数据库
     * @param deployTask 部署任务，deployTask会被更新remark以及deployState
     * @return true表示部署成功，否则表示失败
     */
    public boolean runDeployTask(DeployTaskDO deployTask) {
        log.info("run deploy task. taskId: {}", deployTask.getId());

        // 检查是否到部署时间
        if (!deployTask.attachDeployingTime()) {
            log.info("The deploy task is not on the deploy time. projectId: {}, taskId: {}, deployTime: {}",
                     deployTask.getProjectId(), deployTask.getId(), deployTask.getDeployStartTime().format(DateTimeUtil.DATE_TIME_FORMATTER));
            deployTask.setRemark("还未到部署时间");
            return false;
        }

        DeployProjectDO deployProject = deployProjectDao.selectById(deployTask.getProjectId());
        // 检查项目是否可部署状态，比如废弃的项目不再部署
        if (deployProject.getProjectState().canNotDeploy()) {
            log.warn("The project can not deploy. taskId: {}, projectId: {}", deployTask.getId(), deployProject.getId());
            deployTask.failRemark("项目已废弃");
            deployTaskDao.updateById(deployTask);
            return false;
        }

        // 检查Agent是否可用
        DeployAgentDO deployAgent = deployAgentDao.selectById(deployTask.getAgentId());
        if (!deployAgent.isActive()) {
            log.info("The agent is not active, can not deploy. taskId: {}, agentIp: {}", deployTask.getId(), deployAgent.getIp());
            deployTask.failRemark("Agent未启动");
            deployTaskDao.updateById(deployTask);
            return false;
        }

        DeployProjectVersionDO deployProjectVersion = deployProjectVersionDao.selectById(deployTask.getProjectVersionId());
        SysFileDO deployFile = sysFileDao.selectByUrl(deployProjectVersion.getDeployFileUrl());

        // 组装完整部署信息，并发送到agent执行
        AgentDeployTaskInfo deployTaskInfo = AgentDeployTaskInfo.builder()
            .taskId(deployTask.getId())
            .projectId(deployTask.getProjectId())
            .projectCode(deployProject.getCode())
            .deployRootPath(deployProject.getDeployRootPath())
            .logRootPath(deployProject.getDeployRootPath())
            .params(deployProject.getParamMap())
            .agentId(deployAgent.getId())
            .version(deployProjectVersion.getVersion())
            .deployFileUrl(deployFile.getUrl())
            .deployFileExtension(deployFile.getExtension())
            .build();

        // 发送给Agent通知其部署
        deployTask.setRemark("开始通知Agent部署");
        deployTaskDao.updateById(deployTask);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CommonResult> response;
        try {
            response = restTemplate.postForEntity(AgentConstants.SEND_DEPLOY_TASK_API.replace("{agentIp}", deployAgent.getIp()),
                                                  deployTaskInfo, CommonResult.class);
        } catch (RestClientException e) {
            log.info("send agent task failed. taskId: {}, agentIp: {}", deployTask.getId(), deployAgent.getIp(), e);
            deployTask.failRemark("请求Agent失败");
            deployTaskDao.updateById(deployTask);
            return false;
        }

        // 检查请求Agent返回值是否正常
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            log.info("send agent task failed. taskId: {}, agentIp: {}, response: {}", deployTask.getId(), deployAgent.getIp(), response);
            deployTask.failRemark("请求Agent返回异常");
            deployTaskDao.updateById(deployTask);
            return false;
        }
        return true;
    }
}
