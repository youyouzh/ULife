package com.uusama.module.system.entity.permission;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.uusama.framework.mybatis.entity.BaseConfigDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 用户和角色关联
 *
 * @author uusama
 */
@TableName("system_user_role")
@KeySequence("system_user_role_seq")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserRoleDO extends BaseConfigDO {
    /** 用户 ID */
    private Long userId;
    /** 角色 ID */
    private Long roleId;

}
