package com.uusama.module.system.api.sms.dto.code;

import com.uusama.framework.tool.validation.Mobile;
import com.uusama.module.system.enums.SmsSceneEnum;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 短信验证码的校验 Request DTO
 *
 * @author uusama
 */
@Data
public class SmsCodeValidateReqDTO {

    /**
     * 手机号
     */
    @Mobile
    @NotEmpty(message = "手机号不能为空")
    private String mobile;
    /**
     * 发送场景
     */
    @NotNull(message = "发送场景不能为空")
    private SmsSceneEnum scene;
    /**
     * 验证码
     */
    @NotEmpty(message = "验证码")
    private String code;

}
