package com.uusama.module.monitor.entity;

import com.uusama.module.monitor.domain.DeployState;

import java.time.LocalDateTime;

/**
 * 部署任务部署时机接口
 * @author zhaohai
 */
public interface DeployTaskRunTime {

    /**
     * 获取部署状态
     * @return DeployState
     */
    DeployState getDeployState();

    /**
     * 获取部署开始时间
     * @return deploy time
     */
    LocalDateTime getDeployStartTime();

    /**
     * 检查是否到达部署时间，主要针对定时部署
     * @return true表示到达部署时间
     */
    default boolean attachDeployingTime() {
        return Math.abs(getDeployStartTime().getSecond() - LocalDateTime.now().getSecond()) <= 30;
    }

    /**
     * 是否到达预约部署时间
     * @return true表示到达预约部署时间
     */
    default boolean attachAppointmentDeployTime() {
        return getDeployState() == DeployState.WAITING && attachDeployingTime();
    }

    /**
     * 是否部署超时，部署中持续1个小时视为超时
     * @return true表示部署超时
     */
    default boolean isDeployingOvertime() {
        return (getDeployState() == DeployState.DEPLOYING || getDeployState() == DeployState.STARTING)
            && getDeployStartTime().plusHours(1).isBefore(LocalDateTime.now());
    }

    /**
     * 是否可以重新部署
     * @return true表示可以重新部署
     */
    default boolean canReDeploy() {
        return getDeployState() == DeployState.INIT || getDeployState() == DeployState.FAILURE;
    }
}
