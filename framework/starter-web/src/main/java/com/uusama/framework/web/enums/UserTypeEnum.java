package com.uusama.framework.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 全局用户类型枚举
 */
@AllArgsConstructor
@Getter
public enum UserTypeEnum {
    /**
     * 面向 c 端，普通用户
     */
    MEMBER(1, "会员"),
    /**
     * 面向 b 端，管理后台
     */
    ADMIN(2, "管理员");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(UserTypeEnum::getValue).toArray();

    /**
     * 类型
     */
    private final Integer value;
    /**
     * 类型名
     */
    private final String name;

    public static UserTypeEnum of(Integer value) {
        return Stream.of(values()).filter(v -> v.getValue().equals(value)).findFirst().orElse(null);
    }
}
