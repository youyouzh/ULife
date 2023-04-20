package com.uusama.framework.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局用户类型枚举
 */
@AllArgsConstructor
@Getter
public enum UserTypeEnum {
    /** 面向 c 端，普通用户 */
    MEMBER("会员"),
    /** 面向 b 端，管理后台 */
    ADMIN("管理员"),
    ;

    /**
     * 类型名
     */
    private final String label;
}
