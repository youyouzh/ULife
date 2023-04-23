package com.uusama.module.monitor.agent.service;

import com.uusama.module.monitor.client.MonitorBackendClient;
import com.uusama.module.monitor.domain.AgentDeployTaskInfo;
import com.uusama.module.monitor.domain.DeployEventInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

/**
 * 服务日志监听
 * @author zhaohai
 */
@Slf4j
public class ServiceLogTailListener extends TailerListenerAdapter {
    private static final String EXCEPTION_LOG_PATTERN = "Exception";
    private static final String FINISH_LOG_PATTERN = "JVM running for";

    private final MonitorBackendClient monitorBackendClient;
    private final AgentDeployTaskInfo deployTaskInfo;
    private Tailer tailer;

    ServiceLogTailListener(MonitorBackendClient monitorBackendClient, AgentDeployTaskInfo deployTaskInfo) {
        this.monitorBackendClient = monitorBackendClient;
        this.deployTaskInfo = deployTaskInfo;
    }

    @Override
    public void init(final Tailer tailer) {
        // 缓存 tailer，便于在listen中停止
        this.tailer = tailer;
    }

    @Override
    public void fileNotFound() {
        log.error("the log file is not exist.");
        monitorBackendClient.reportEvents(DeployEventInfo.fail(deployTaskInfo, "服务日志文件未找到"));
        this.tailer.stop();
    }

    @Override
    public void handle(final String line) {
        // 出现异常记录
        if (line.matches(EXCEPTION_LOG_PATTERN)) {
            log.warn("exist Exception.");
            // 上报日志
            monitorBackendClient.reportEvents(DeployEventInfo.fail(deployTaskInfo, "服务启动时发生异常"));
        }

        // 启动完成
        if (line.matches(FINISH_LOG_PATTERN)) {
            log.info("JVM started success.");
            monitorBackendClient.reportEvents(DeployEventInfo.normal(deployTaskInfo, "服务启动成功"));
            tailer.stop();
        }
    }
}
