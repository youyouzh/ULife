package com.uusama.module.system.entity.permission;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.uusama.framework.mybatis.entity.BaseConfigDO;
import com.uusama.framework.mybatis.type.JsonLongSetTypeHandler;
import com.uusama.framework.web.enums.CommonState;
import com.uusama.module.system.enums.DataScopeEnum;
import com.uusama.module.system.enums.RoleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * 角色 DO
 *
 * @author uusama
 */
@TableName(value = "system_role", autoResultMap = true)
@KeySequence("system_role_seq")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RoleDO extends BaseConfigDO {

    /** 角色名称 */
    private String name;
    /**
     * 角色标识
     *
     * 枚举
     */
    private String code;
    /** 角色排序 */
    private Integer sort;
    /** 角色状态 */
    @EnumValue
    private CommonState state;
    /** 角色类型 */
    @EnumValue
    private RoleTypeEnum type;
    /** 备注 */
    private String remark;

    /** 数据范围 */
    @EnumValue
    private DataScopeEnum dataScope;
    /**
     * 数据范围(指定部门数组)
     *
     * 适用于 {@link #dataScope} 的值为 {@link DataScopeEnum#DEPT_CUSTOM} 时
     */
    @TableField(typeHandler = JsonLongSetTypeHandler.class)
    private Set<Long> dataScopeDeptIds;

}
