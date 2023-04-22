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
 * 角色和菜单关联
 *
 * @author uusama
 */
@TableName("system_role_menu")
@KeySequence("system_role_menu_seq")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RoleMenuDO extends BaseConfigDO {
    /** 角色ID */
    private Long roleId;
    /** 菜单ID */
    private Long menuId;

}
