package com.uusama.module.monitor.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.uusama.common.util.DateTimeUtil;
import com.uusama.module.monitor.constant.AgentConstants;
import com.uusama.module.monitor.domain.DeployState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 具体部署任务信息
 * @author zhaohai
 */
@TableName("deploy_task")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeployTaskDO extends BaseDO implements DeployTaskRunTime {

    /**
     * 项目id，属于哪个项目，DeployProject的主键
     */
    private int projectId;

    /**
     * 部署版本id，对应DeployProjectVersion
     */
    private int projectVersionId;

    /**
     * 部署的服务器agentID，DeployAgent的主键
     */
    private int agentId;

    /**
     * 批量部署任务ID，如果不为空，则表示率属于某个批量部署任务，否知是单独的任务
     */
    private Integer batchTaskId;

    /**
     * 部署开始时间，多次部署只记录最后一次时间，可以定时部署
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deployStartTime;

    /**
     * 部署结束时间，包括报错和成功等
     */
    private LocalDateTime deployEndTime;

    /**
     * 当前部署状态
     */
    @EnumValue
    private DeployState deployState;

    /**
     * 当前部署进度，0-100的指
     */
    private int progress;

    /**
     * 部署成功的实际耗时，单位毫秒
     */
    private int deployDurationMs;

    /**
     * 部署时的参数列表json字符
     */
    private String params;

    /**
     * 部署任务执行的备注或相关消息
     */
    private String remark;

    /**
     * 部署过程日志，remark日志
     */
    private String deployLog;

    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /**
     * 部署失败，并记录备注
     * @param remark 备注信息
     */
    public void failRemark(String remark) {
        this.deployState = DeployState.FAILURE;
        this.remark = remark;
        appendDeployLog(this.remark);
    }

    /**
     * 判断是否预约部署任务
     * @param deployStartTime 部署时间
     * @return true表示预约部署
     */
    public static boolean isWaitingDeploy(LocalDateTime deployStartTime) {
        return deployStartTime.isAfter(LocalDateTime.now().plusMinutes(1));
    }

    /**
     * 初始化部署时间和状态，状态根据时间来确定
     * @param deployStartTime 部署时间
     */
    public void initDeployStateAndTime(LocalDateTime deployStartTime) {
        this.deployStartTime = Objects.isNull(deployStartTime) ? LocalDateTime.now() : deployStartTime;
        this.deployState = isWaitingDeploy(deployStartTime) ? DeployState.WAITING : DeployState.INIT;
    }

    /**
     * 附加部署日志
     * @param deployLog 部署日志
     */
    public void appendDeployLog(String deployLog) {
        // 避免null转字符串“null”
        this.deployLog = StringUtils.isBlank(this.deployLog) ? "" : this.deployLog;
        this.deployLog += AgentConstants.DEPLOY_LOG_LINE_TEMPLATE
            .replace("{time}", LocalDateTime.now().format(DateTimeUtil.DATE_TIME_FORMATTER))
            .replace("{log}", deployLog);
    }

    /**
     * 重置部署信息
     */
    public void resetDeploy() {
        deployState = DeployState.INIT;
        deployStartTime = LocalDateTime.now();
        remark = "开始部署...";
        appendDeployLog(remark);
    }

    @Override
    public LambdaQueryWrapper<DeployTaskDO> getUniqueQuery() {
        if (Objects.isNull(batchTaskId)) {
            return new LambdaQueryWrapper<DeployTaskDO>().eq(true, DeployTaskDO::getId, id);
        }
        // 相同batchTaskId不重复创建
        return new LambdaQueryWrapper<DeployTaskDO>()
            .eq(DeployTaskDO::getProjectId, projectId)
            .eq(DeployTaskDO::getProjectVersionId, projectVersionId)
            .eq(DeployTaskDO::getAgentId, agentId)
            .eq(DeployTaskDO::getBatchTaskId, batchTaskId);
    }
}
