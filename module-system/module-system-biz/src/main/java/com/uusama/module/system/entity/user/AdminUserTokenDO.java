package com.uusama.module.system.entity.user;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.uusama.framework.mybatis.entity.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 管理后台的用户token记录表
 * @author uusama
 */
@TableName(value = "system_user_token", autoResultMap = true)
@KeySequence("system_user_token_seq")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AdminUserTokenDO extends BaseDO {
    /** 用户id */
    private Long userId;
    /** 客户端唯一标识 */
    private String clientId;
    /** 访问令牌 */
    private String accessToken;
    /** 刷新令牌 */
    private String refreshToken;
    /** 访问令牌过期时间 */
    private LocalDateTime accessTokenExpireTime;
    /** 刷新令牌过期时间 */
    private LocalDateTime refreshTokenExpireTime;
}
