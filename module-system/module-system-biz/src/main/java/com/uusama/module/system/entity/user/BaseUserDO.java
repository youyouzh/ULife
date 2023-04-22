package com.uusama.module.system.entity.user;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.uusama.framework.mybatis.entity.BaseDO;
import com.uusama.framework.web.enums.UserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 用于用户关联表，区分用户和用户类型
 *
 * @author uusama
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseUserDO extends BaseDO {
    /** 用户编号 */
    private Long userId;
    /** 用户类型 */
    @EnumValue
    private UserTypeEnum userType;
}
