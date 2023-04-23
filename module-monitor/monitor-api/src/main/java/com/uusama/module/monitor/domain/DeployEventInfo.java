package com.uusama.module.monitor.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author zhaohai
 * 部署事件信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeployEventInfo {
    @Schema(description = "部署任务ID")
    private int taskId;

    @Schema(description = "部署信息")
    private String message;

    @Schema(description = "是否部署失败")
    private boolean failed;

    @Schema(description = "部署状态")
    private DeployState deployState;

    @Schema(description = "事件发生时间")
    private LocalDateTime eventTime;

    public static DeployEventInfo normal(AgentDeployTaskInfo deployTaskInfo, String message) {
        return DeployEventInfo.builder()
            .taskId(deployTaskInfo.getTaskId())
            .message(message)
            .deployState(DeployState.DEPLOYING)
            .eventTime(LocalDateTime.now())
            .build();
    }

    public static DeployEventInfo fail(AgentDeployTaskInfo deployTaskInfo, String message) {
        DeployEventInfo deployEventInfo = normal(deployTaskInfo, message);
        deployEventInfo.setDeployState(DeployState.FAILURE);
        return deployEventInfo;
    }

    public static DeployEventInfo startup(AgentDeployTaskInfo deployTaskInfo, String message) {
        DeployEventInfo deployEventInfo = normal(deployTaskInfo, message);
        deployEventInfo.setDeployState(DeployState.STARTING);
        return deployEventInfo;
    }
}
