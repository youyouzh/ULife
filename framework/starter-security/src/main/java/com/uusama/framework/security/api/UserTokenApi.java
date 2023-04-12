package com.uusama.framework.security.api;

import com.uusama.framework.security.LoginUser;

/**
 * OAuth2.0 Token API 接口
 * @author uusama
 */
public interface UserTokenApi {

    /**
     * 校验访问令牌
     *
     * @param accessToken 访问令牌
     * @return 访问令牌的信息
     */
    LoginUser checkUserToken(String accessToken);
}
