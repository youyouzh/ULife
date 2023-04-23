package com.uusama.module.monitor.domain;

import lombok.Getter;

@Getter
public enum DeployState {
    /**
     * 部署状态
     */
    INIT("初始化"),

    /**
     * 预约定时部署时的状态
     */
    WAITING("等待部署"),
    DEPLOYING("部署中"),
    STARTING("服务启动中"),
    SUCCESS("部署成功"),
    FAILURE("部署失败"),
    ;

    private final String description;

    DeployState(String description) {
        this.description = description;
    }
}