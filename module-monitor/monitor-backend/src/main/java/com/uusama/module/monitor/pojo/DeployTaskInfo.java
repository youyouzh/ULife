package com.uusama.module.monitor.pojo;

import com.uusama.module.monitor.entity.DeployAgentDO;
import com.uusama.module.monitor.entity.DeployProjectDO;
import com.uusama.module.monitor.entity.DeployProjectVersionDO;
import com.uusama.module.monitor.entity.DeployTaskDO;
import com.uusama.module.monitor.domain.DeployState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author zhaohai
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeployTaskInfo {
    @Schema(description = "任务id")
    private int id;

    @Schema(description = "部署项目信息")
    private DeployProjectDO deployProject;

    @Schema(description = "部署项目版本")
    private DeployProjectVersionDO deployProjectVersion;

    @Schema(description = "部署机器agent信息")
    private DeployAgentDO deployAgent;

    @Schema(description = "批量部署任务ID，如果不为空，则表示率属于某个批量部署任务，否知是单独的任务")
    private Integer batchTaskId;

    private LocalDateTime deployStartTime;

    private LocalDateTime deployEndTime;

    private DeployState deployState;

    @Schema(description = "部署进度")
    private int progress;

    private String remark;

    public static DeployTaskInfo of(DeployTaskDO deployTask, DeployProjectDO deployProject,
                                    DeployProjectVersionDO deployProjectVersion, DeployAgentDO deployAgent) {
        return DeployTaskInfo.builder()
            .id(deployTask.getId())
            .deployProject(deployProject)
            .deployProjectVersion(deployProjectVersion)
            .deployAgent(deployAgent)
            .batchTaskId(deployTask.getBatchTaskId())
            .deployStartTime(deployTask.getDeployStartTime())
            .deployEndTime(deployTask.getDeployEndTime())
            .deployState(deployTask.getDeployState())
            .remark(deployTask.getRemark())
            .build();
    }
}
