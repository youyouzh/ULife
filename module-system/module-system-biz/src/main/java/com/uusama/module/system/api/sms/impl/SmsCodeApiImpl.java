package com.uusama.module.system.api.sms.impl;

import com.uusama.module.system.api.sms.SmsCodeApi;
import com.uusama.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import com.uusama.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import com.uusama.module.system.api.sms.dto.code.SmsCodeValidateReqDTO;
import org.springframework.stereotype.Component;

/**
 * @author uusama
 */
@Component
public class SmsCodeApiImpl implements SmsCodeApi {
    @Override
    public void sendSmsCode(SmsCodeSendReqDTO reqDTO) {

    }

    @Override
    public void useSmsCode(SmsCodeUseReqDTO reqDTO) {

    }

    @Override
    public void validateSmsCode(SmsCodeValidateReqDTO reqDTO) {

    }
}
