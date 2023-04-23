package com.uusama.module.monitor.pojo;

import com.uusama.module.monitor.entity.DeployProjectVersionDO;
import com.uusama.module.monitor.entity.SysFileDO;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeployProjectVersionInfo {
    private int id;

    private int projectId;

    private String version;

    @Schema(description = "参考版本")
    private String referenceVersion;

    @Schema(description = "该版本变更日志")
    private String changeLog;

    @Schema(description = "安装包文件信息")
    private SysFileDO sysFile;

    private LocalDateTime createdAt;

    public static DeployProjectVersionInfo of(DeployProjectVersionDO deployProjectVersion, SysFileDO sysFile) {
        return DeployProjectVersionInfo.builder()
            .id(deployProjectVersion.getId())
            .projectId(deployProjectVersion.getProjectId())
            .version(deployProjectVersion.getVersion())
            .referenceVersion(deployProjectVersion.getReferenceVersion())
            .changeLog(deployProjectVersion.getChangeLog())
            .sysFile(sysFile)
            .createdAt(deployProjectVersion.getCreatedAt())
            .build();
    }
}
