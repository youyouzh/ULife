package com.uusama.module.system.entity.logger;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.uusama.module.system.entity.user.BaseUserDO;
import com.uusama.module.system.logger.LoginLogTypeEnum;
import com.uusama.module.system.logger.LoginResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 登录日志表
 *
 * 注意，包括登录和登出两种行为
 *
 * @author uusama
 */
@TableName("system_login_log")
@KeySequence("system_login_log_seq")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LoginLogDO extends BaseUserDO {
    /** 日志类型 */
    @EnumValue
    private LoginLogTypeEnum logType;
    /** 链路追踪编号 */
    private String traceId;
    /**
     * 用户账号
     * 冗余，因为账号可以变更
     */
    private String username;
    /** 登录结果 */
    @EnumValue
    private LoginResultEnum result;
    /** 用户 IP */
    private String userIp;
    /** 浏览器 UA */
    private String userAgent;

}
