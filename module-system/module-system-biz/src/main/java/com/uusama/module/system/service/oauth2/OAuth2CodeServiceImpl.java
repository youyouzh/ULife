package com.uusama.module.system.service.oauth2;

import com.uusama.common.util.DateTimeUtil;
import com.uusama.common.util.IdUtil;
import com.uusama.framework.web.enums.UserTypeEnum;
import com.uusama.module.system.entity.oauth2.OAuth2CodeDO;
import com.uusama.module.system.mapper.oauth2.OAuth2CodeMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static com.uusama.framework.web.exception.ServiceExceptionUtil.exception;
import static com.uusama.module.system.constant.ErrorCodeConstants.OAUTH2_CODE_EXPIRE;
import static com.uusama.module.system.constant.ErrorCodeConstants.OAUTH2_CODE_NOT_EXISTS;

/**
 * OAuth2.0 授权码 Service 实现类
 *
 * @author uusama
 */
@Service
@Validated
public class OAuth2CodeServiceImpl implements OAuth2CodeService {

    /**
     * 授权码的过期时间，默认 5 分钟
     */
    private static final Integer TIMEOUT = 5 * 60;

    @Resource
    private OAuth2CodeMapper oauth2CodeMapper;

    @Override
    public OAuth2CodeDO createAuthorizationCode(Long userId, UserTypeEnum userType, String clientId,
                                                List<String> scopes, String redirectUri, String state) {
        OAuth2CodeDO codeDO = OAuth2CodeDO.builder().code(generateCode())
                .userId(userId).userType(userType)
                .clientId(clientId).scopes(scopes)
                .expiresTime(LocalDateTime.now().plusSeconds(TIMEOUT))
                .redirectUri(redirectUri).state(state).build();
        oauth2CodeMapper.insert(codeDO);
        return codeDO;
    }

    @Override
    public OAuth2CodeDO consumeAuthorizationCode(String code) {
        OAuth2CodeDO codeDO = oauth2CodeMapper.selectByCode(code);
        if (codeDO == null) {
            throw exception(OAUTH2_CODE_NOT_EXISTS);
        }
        if (DateTimeUtil.isExpired(codeDO.getExpiresTime())) {
            throw exception(OAUTH2_CODE_EXPIRE);
        }
        oauth2CodeMapper.deleteById(codeDO.getId());
        return codeDO;
    }

    private static String generateCode() {
        return IdUtil.fastSimpleUUID();
    }

}
