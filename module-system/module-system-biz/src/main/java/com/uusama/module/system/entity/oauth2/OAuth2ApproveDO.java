package com.uusama.module.system.entity.oauth2;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.uusama.module.system.entity.user.BaseUserDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * OAuth2 批准 DO
 *
 * 用户在 sso.vue 界面时，记录接受的 scope 列表
 *
 * @author uusama
 */
@TableName(value = "system_oauth2_approve", autoResultMap = true)
@KeySequence("system_oauth2_approve_seq")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OAuth2ApproveDO extends BaseUserDO {

    /**
     * 客户端编号
     *
     * 关联 {@link OAuth2ClientDO#getId()}
     */
    private String clientId;
    /** 授权范围 */
    private String scope;
    /**
     * 是否接受
     *
     * true - 接受
     * false - 拒绝
     */
    private Boolean approved;
    /** 过期时间 */
    private LocalDateTime expiresTime;

}
