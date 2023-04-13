package com.uusama.module.system.controller.admin.permission.vo.role;

import com.alibaba.excel.annotation.ExcelProperty;
import com.uusama.framework.tool.annotation.DictFormat;
import com.uusama.framework.tool.convert.DictConvert;
import com.uusama.framework.web.enums.CommonState;
import com.uusama.module.system.constant.DictTypeConstants;
import com.uusama.module.system.enums.DataScopeEnum;
import lombok.Data;

/**
 * 角色 Excel 导出响应 VO
 */
@Data
public class RoleExcelVO {

    @ExcelProperty("角色序号")
    private Long id;

    @ExcelProperty("角色名称")
    private String name;

    @ExcelProperty("角色标志")
    private String code;

    @ExcelProperty("角色排序")
    private Integer sort;

    @ExcelProperty("数据范围")
    private DataScopeEnum dataScope;

    @ExcelProperty(value = "角色状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.COMMON_STATUS)
    private CommonState state;

}
