package com.uusama.module.system.controller.admin.permission.vo.role;

import com.uusama.framework.web.enums.CommonState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.uusama.common.util.DateTimeUtil.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 角色分页 Request VO")
@Data
public class RoleExportReqVO {

    @Schema(description = "角色名称,模糊匹配", example = "uusama")
    private String name;

    @Schema(description = "角色标识,模糊匹配", example = "uusama")
    private String code;

    @Schema(description = "展示状态,参见 CommonState 枚举类", example = "1")
    private CommonState state;

    @Schema(description = "开始时间", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
