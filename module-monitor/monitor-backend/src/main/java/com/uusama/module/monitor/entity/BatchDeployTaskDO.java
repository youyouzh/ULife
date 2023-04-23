package com.uusama.module.monitor.entity;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.uusama.module.monitor.domain.DeployState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author zhaohai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchDeployTaskDO extends BaseDO implements DeployTaskRunTime {
    private String title;

    private String changeLog;

    /**
     * 具体的部署微服务和服务器列表，json字符串
     */
    private String deployTaskIds;

    /**
     * 部署状态
     */
    private DeployState deployState;

    /**
     * 部署备注描述
     */
    private String remark;

    /**
     * 部署总进度，0-100的值，全部任务进度平均值
     */
    private int progress;

    private LocalDateTime deployStartTime;
    private LocalDateTime deployEndTime;

//    private String createUser;
//    private String deployUser;

    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /**
     * 初始化部署时间和状态，状态根据时间来确定
     * @param deployStartTime 部署时间
     */
    public void initDeployStateAndTime(LocalDateTime deployStartTime) {
        this.deployStartTime = Objects.isNull(deployStartTime) ? LocalDateTime.now() : deployStartTime;
        this.deployState = DeployTaskDO.isWaitingDeploy(deployStartTime) ? DeployState.WAITING : DeployState.INIT;
    }

    @Override
    public LambdaQueryWrapper<BatchDeployTaskDO> getUniqueQuery() {
        return new LambdaQueryWrapper<BatchDeployTaskDO>().eq(StringUtils.isNotBlank(changeLog), BatchDeployTaskDO::getChangeLog, changeLog);
    }
}
