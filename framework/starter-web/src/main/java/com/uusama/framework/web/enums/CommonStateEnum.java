package com.uusama.framework.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态枚举
 * @author uusama
 */
@Getter
@AllArgsConstructor
public enum CommonStateEnum {
    ENABLE("开启"),
    DISABLE("关闭");

    /**
     * 状态名
     */
    private final String name;
}
