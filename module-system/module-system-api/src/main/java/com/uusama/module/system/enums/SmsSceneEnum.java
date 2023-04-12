package com.uusama.module.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户短信验证码发送场景的枚举
 *
 * @author uusama
 */
@Getter
@AllArgsConstructor
public enum SmsSceneEnum {

    MEMBER_LOGIN(1, "user-sms-login", "会员用户 - 手机号登陆"),
    MEMBER_UPDATE_MOBILE(2, "user-sms-reset-password", "会员用户 - 修改手机"),
    MEMBER_FORGET_PASSWORD(3, "user-sms-update-mobile", "会员用户 - 忘记密码"),

    ADMIN_MEMBER_LOGIN(21, "admin-sms-login", "后台用户 - 手机号登录");

    /**
     * 验证场景的编号
     */
    private final Integer scene;
    /**
     * 模版编码
     */
    private final String templateCode;
    /**
     * 描述
     */
    private final String description;

}
