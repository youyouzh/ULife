package com.uusama.module.system.entity.oauth2;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.uusama.module.system.entity.user.BaseUserDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OAuth2 访问令牌 DO
 *
 * 如下字段，暂时未使用，暂时不支持：
 * user_name、authentication（用户信息）
 *
 * @author uusama
 */
@TableName(value = "system_oauth2_access_token", autoResultMap = true)
@KeySequence("system_oauth2_access_token_seq")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OAuth2AccessTokenDO extends BaseUserDO {

    /** 访问令牌 */
    private String accessToken;
    /** 刷新令牌 */
    private String refreshToken;
    /**
     * 客户端编号
     *
     * 关联 {@link OAuth2ClientDO#getId()}
     */
    private String clientId;
    /** 授权范围 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> scopes;
    /** 过期时间 */
    private LocalDateTime expiresTime;

}
