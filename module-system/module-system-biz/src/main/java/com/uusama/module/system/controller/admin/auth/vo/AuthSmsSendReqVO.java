package com.uusama.module.system.controller.admin.auth.vo;

import com.uusama.framework.tool.validation.Mobile;
import com.uusama.module.system.enums.SmsSceneEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 发送手机验证码 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSmsSendReqVO {

    @Schema(description = "手机号", required = true, example = "18258432912")
    @NotEmpty(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @Schema(description = "短信场景", required = true, example = "1")
    @NotNull(message = "发送场景不能为空")
    private SmsSceneEnum scene;

}
