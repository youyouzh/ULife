package com.uusama.module.system.entity.dept;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.uusama.framework.mybatis.entity.BaseDO;
import com.uusama.framework.web.enums.CommonState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 岗位表
 *
 * @author uusama
 */
@TableName("system_post")
@KeySequence("system_post_seq")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PostDO extends BaseDO {
    /** 岗位名称 */
    private String name;
    /** 岗位编码 */
    private String code;
    /** 岗位排序 */
    private Integer sort;
    /** 状态 */
    @EnumValue
    private CommonState state;
    /** 备注 */
    private String remark;

}
