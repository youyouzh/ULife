package com.uusama.module.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;

/**
 * @author zhaohai
 * 部署的项目版本
 */
@TableName("deploy_project_version")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeployProjectVersionDO extends BaseDO {

    private int projectId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 参考版本
     */
    private String referenceVersion;

    /**
     * 该版本变更日志
     */
    private String changeLog;

    /**
     * 部署文件下载地址
     */
    private String deployFileUrl;

    private LocalDateTime updatedAt;

    @Override
    public LambdaQueryWrapper<DeployProjectVersionDO> getUniqueQuery() {
        return new LambdaQueryWrapper<DeployProjectVersionDO>()
            .eq(true, DeployProjectVersionDO::getProjectId, this.getProjectId())
            .eq(StringUtils.isNotBlank(this.getVersion()), DeployProjectVersionDO::getVersion, this.getVersion());
    }
}
