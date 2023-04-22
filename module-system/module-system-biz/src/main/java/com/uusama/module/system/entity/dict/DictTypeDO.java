package com.uusama.module.system.entity.dict;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.uusama.framework.mybatis.entity.BaseDO;
import com.uusama.framework.web.enums.CommonState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 字典类型表
 *
 * @author uusama
 */
@TableName("system_dict_type")
@KeySequence("system_dict_type_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictTypeDO extends BaseDO {
    /** 字典名称 */
    private String name;
    /** 字典类型 */
    private String type;
    /** 状态 */
    @EnumValue
    private CommonState state;
    /** 备注 */
    private String remark;

    /** 删除时间 */
    private LocalDateTime deletedTime;

}
