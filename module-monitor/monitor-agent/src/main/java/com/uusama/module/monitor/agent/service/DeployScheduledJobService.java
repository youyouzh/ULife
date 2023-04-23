package com.uusama.module.monitor.agent.service;

import com.netflix.appinfo.InstanceInfo;
import com.uusama.module.monitor.agent.exception.AgentDeployException;
import com.uusama.module.monitor.client.MonitorBackendClient;
import com.uusama.module.monitor.domain.AgentDeployTaskInfo;
import com.uusama.module.monitor.domain.DeployEventInfo;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaohai
 * 部署定时任务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeployScheduledJobService {
    private static final int MAX_TAIL_LISTEN_MINUTE = 5;
    private static final int TAIL_LISTEN_STOP_POOL_SIZE = 20;

    private final MonitorBackendClient monitorBackendClient;
    private final List<RunScriptHandler> runScriptHandlers;
    private final DiscoveryClient discoveryClient;

    /**
     * tail -f 服务日志监听线程池
     */
    private ExecutorService tailListenerExecutor;
    private ScheduledExecutorService tailListenerStopExecutor;

    @PostConstruct
    public void init() {
        tailListenerExecutor = new ThreadPoolExecutor(TAIL_LISTEN_STOP_POOL_SIZE, TAIL_LISTEN_STOP_POOL_SIZE * 2, 10,
                                                      TimeUnit.MINUTES, new LinkedBlockingQueue<>(100), new CustomizableThreadFactory("tailListenerExecutor"));
        tailListenerStopExecutor = new ScheduledThreadPoolExecutor(TAIL_LISTEN_STOP_POOL_SIZE, new CustomizableThreadFactory("tailListenerStopExecutor"));
    }

    /**
     * agent心跳上报
     */
    @Scheduled(fixedDelay = 5000)
    public void heartbeat() {
        Optional<String> localhost = getLocalHost();
        if (!localhost.isPresent()) {
            log.error("get Localhost error.");
            return;
        }
        monitorBackendClient.agentHeartbeat(localhost.get());
    }

    @Async
    public void asyncRunDeploy(AgentDeployTaskInfo deployTaskInfo) {
        try {
            this.runDirectDeploy(deployTaskInfo);
        } catch (AgentDeployException e) {
            log.error("agent deploy exception. taskId: {}", deployTaskInfo.getTaskId(), e);
            monitorBackendClient.reportEvents(DeployEventInfo.fail(deployTaskInfo, e.getMessage()));
        }
    }

    /**
     * 直接执行部署，发送异常情况直接抛出异常
     * @param deployTaskInfo task
     */
    private void runDirectDeploy(AgentDeployTaskInfo deployTaskInfo) {
        // 下载文件，使用open feign方式下载，或者使用专门文件服务
        recordLog(deployTaskInfo,"开始下载部署包");
        readyDeployPackage(deployTaskInfo);
        recordLog(deployTaskInfo, "下载部署包完成，准备部署脚本");

        // 生成部署脚本
        RunScriptHandler handler = runScriptHandlers.stream()
            .filter(v -> v.accept(deployTaskInfo)).findFirst()
            .orElseThrow(() -> new AgentDeployException("还不支持该容器部署"));
        handler.readyRunScript(deployTaskInfo);

        // Eureka服务注销
        recordLog(deployTaskInfo, "注销Eureka服务");
        unregisterEurekaService(deployTaskInfo);

        // 执行部署脚本，先关闭已经移动服务，再部署新服务
        recordLog(deployTaskInfo, "开始运行停止脚本");
        handler.runStop(deployTaskInfo);
        recordLog(deployTaskInfo, "开始运行启动脚本");
        handler.runStart(deployTaskInfo);

        // 监听启动服务日志
        monitorBackendClient.reportEvents(DeployEventInfo.startup(deployTaskInfo, "服务启动中..."));
        listenServiceLog(deployTaskInfo);
    }

    private void unregisterEurekaService(AgentDeployTaskInfo deployTaskInfo) {
        // 现阶段是闭市后部署，不考虑可持续性部署，否则需要提前注销eureka服务，并等待30s左右等待请求处理完
        String localIp = getLocalHost().orElse("");
        Optional<InstanceInfo> instanceInfo = discoveryClient.getInstances(deployTaskInfo.getProjectCode()).stream()
            .map(v -> ((EurekaServiceInstance) v).getInstanceInfo())
            .filter(v -> getLocalHost().orElse("").equals(v.getIPAddr()))
            .filter(v -> v.getStatus() == InstanceInfo.InstanceStatus.UP)
            .findFirst();
        if (instanceInfo.isPresent()) {
            log.info("unregister eureka service. serviceId: {}, agentIp: {}", deployTaskInfo.getProjectCode(), localIp);
            // 处理所有eureka的注销，delete请求，通过配置获取eureka地址列表
            // RestTemplate restTemplate = new RestTemplate();
            // restTemplate.delete("/eureka/apps/{serverName}/{Instance}");
        }
    }

    private void listenServiceLog(AgentDeployTaskInfo deployTaskInfo) {
        // 监听服务启动日志
        TailerListener listener = new ServiceLogTailListener(monitorBackendClient, deployTaskInfo);
        Tailer tailer = new Tailer(new File(deployTaskInfo.getServiceLogPath()), listener);
        tailListenerExecutor.submit(tailer);
        // 这个定时停止，可以优化一下
        tailListenerStopExecutor.schedule(tailer::stop, MAX_TAIL_LISTEN_MINUTE, TimeUnit.MINUTES);
    }

    private void readyDeployPackage(AgentDeployTaskInfo deployTaskInfo) {
        try (Response response = monitorBackendClient.downloadFile(deployTaskInfo.getDeployFileUrl())) {
            if (response.status() != HttpStatus.OK.value()) {
                log.info("download deploy package failed. taskId: {}, fileUrl: {}", deployTaskInfo.getTaskId(), deployTaskInfo.getDeployFileUrl());
                throw new AgentDeployException("下载部署包失败");
            }

            File downloadFile = new File(deployTaskInfo.getPackageSavePath());
            FileUtils.copyInputStreamToFile(response.body().asInputStream(), downloadFile);
        } catch (IOException e) {
            log.error("save deploy package failed. taskId: {}, fileUrl: {}, savePath: {}", deployTaskInfo.getTaskId(),
                      deployTaskInfo.getDeployFileUrl(), deployTaskInfo.getPackageSavePath());
            throw new AgentDeployException("写入部署包失败");
        }
    }

    private void recordLog(AgentDeployTaskInfo deployTaskInfo, String message) {
        monitorBackendClient.reportEvents(DeployEventInfo.normal(deployTaskInfo, message));
    }

    private Optional<String> getLocalHost() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return Optional.of(address.getHostAddress());
        } catch (UnknownHostException e) {
            log.error("get local host error.");
            return Optional.empty();
        }
    }
}
