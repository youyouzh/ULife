package com.uusama.module.system.controller.admin.user.vo.user;

import com.alibaba.excel.annotation.ExcelProperty;
import com.uusama.framework.tool.annotation.DictFormat;
import com.uusama.framework.tool.convert.DictConvert;
import com.uusama.framework.web.enums.CommonState;
import com.uusama.module.system.constant.DictTypeConstants;
import com.uusama.module.system.entity.user.AdminUserDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户 Excel 导入 VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = false) // 设置 chain = false，避免用户导入有问题
public class UserImportExcelVO {

    @ExcelProperty("登录名称")
    private String username;

    @ExcelProperty("用户名称")
    private String nickname;

    @ExcelProperty("部门编号")
    private Long deptId;

    @ExcelProperty("用户邮箱")
    private String email;

    @ExcelProperty("手机号码")
    private String mobile;

    @ExcelProperty(value = "用户性别", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.USER_SEX)
    private AdminUserDO.SexEnum sex;

    @ExcelProperty(value = "账号状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.COMMON_STATUS)
    private CommonState state;

}
