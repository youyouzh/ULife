package com.uusama.module.system.entity.dict;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.uusama.framework.mybatis.entity.BaseDO;
import com.uusama.framework.web.enums.CommonState;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据表
 *
 * @author uusama
 */
@TableName("system_dict_data")
@KeySequence("system_dict_data_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictDataDO extends BaseDO {
    /** 字典排序 */
    private Integer sort;
    /** 字典标签 */
    private String label;
    /** 字典值 */
    private String value;
    /**
     * 字典类型
     *
     * 冗余 {@link DictDataDO#getDictType()}
     */
    private String dictType;
    /** 状态 */
    @EnumValue
    private CommonState state;
    /**
     * 颜色类型
     *
     * 对应到 element-ui 为 default、primary、success、info、warning、danger
     */
    private String colorType;
    /** css 样式 */
    private String cssClass;
    /** 备注 */
    private String remark;

}
