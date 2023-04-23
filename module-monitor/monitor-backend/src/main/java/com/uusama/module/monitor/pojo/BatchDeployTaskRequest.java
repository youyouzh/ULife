package com.uusama.module.monitor.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.uusama.common.util.DateTimeUtil;
import com.uusama.module.monitor.entity.BatchDeployTaskDO;
import com.uusama.module.monitor.entity.DeployAgentDO;
import com.uusama.module.monitor.entity.DeployProjectDO;
import com.uusama.module.monitor.entity.DeployProjectVersionDO;
import com.uusama.module.monitor.entity.DeployTaskDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhaohai
 * 批量部署任务创建请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchDeployTaskRequest {
    @Schema(description = "部署标题")
    private String title;

    @Schema(description = "修改日志")
    private String changeLog;

    @Schema(description = "批量部署压缩包路径，在上传时自动解析")
    private String zipUrl;

    @DateTimeFormat(pattern = DateTimeUtil.DATE_TIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deployStartTime = LocalDateTime.now();

    @Schema(description = "适用于指定projectId等能创建部署任务的场景")
    private List<DeployTaskDO> deployTasks;

    @Schema(description = "需要部署的项目列表")
    private List<DeployProjectWrapper> deployProjectWrappers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeployProjectWrapper {
        @Schema(description = "部署项目，如果id=0则表示新建项目")
        private DeployProjectDO deployProject;

        @Schema(description = "部署的版本，如果id=0或者version不存在则表示新建版本，并且deployFileUrl表示上传文件相对路径")
        private DeployProjectVersionDO deployProjectVersion;

        @Schema(description = "需要部署的Agent列表，只需给定id或者ip即可")
        private List<DeployAgentDO> deployAgents;

        @Schema(description = "支持指定每个项目的部署时间")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime deployStartTime;
    }

    public BatchDeployTaskDO wrapBatchTask() {
        BatchDeployTaskDO batchDeployTask = BatchDeployTaskDO.builder()
            .title(title)
            .changeLog(changeLog)
            .build();
        batchDeployTask.initDeployStateAndTime(deployStartTime);
        return batchDeployTask;
    }

    public List<DeployTaskDO> wrapTasks() {
        deployTasks.forEach(v -> v.initDeployStateAndTime(v.getDeployStartTime()));
        return deployTasks;
    }
}
