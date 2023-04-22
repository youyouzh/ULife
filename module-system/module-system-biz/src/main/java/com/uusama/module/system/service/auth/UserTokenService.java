package com.uusama.module.system.service.auth;

import com.uusama.module.system.controller.admin.auth.vo.AuthLoginRespVO;

import java.util.Optional;

/**
 * OAuth2.0 Token Service 接口
 *
 * 从功能上，和 Spring Security OAuth 的 DefaultTokenServices + JdbcTokenStore 的功能，提供访问令牌、刷新令牌的操作
 *
 * @author uusama
 */
public interface UserTokenService {

    /**
     * 创建访问令牌
     * 注意：该流程中，会包含创建刷新令牌的创建
     *
     * 参考 DefaultTokenServices 的 createAccessToken 方法
     *
     * @param userId 用户编号
     * @param clientId 客户端编号
     * @return 访问令牌的信息
     */
    AuthLoginRespVO createAccessToken(Long userId, String clientId);

    /**
     * 刷新访问令牌
     *
     * 参考 DefaultTokenServices 的 refreshAccessToken 方法
     *
     * @param refreshToken 刷新令牌
     * @param clientId 客户端编号
     * @return 访问令牌的信息
     */
    AuthLoginRespVO refreshAccessToken(String refreshToken, String clientId);

    /**
     * 获得访问令牌
     *
     * 参考 DefaultTokenServices 的 getAccessToken 方法
     *
     * @param accessToken 访问令牌
     * @return 访问令牌的信息
     */
    AuthLoginRespVO getAccessToken(String accessToken);

    /**
     * 校验访问令牌
     *
     * @param accessToken 访问令牌
     * @return 访问令牌的信息
     */
    AuthLoginRespVO checkAccessToken(String accessToken);

    /**
     * 移除访问令牌
     * 注意：该流程中，会移除相关的刷新令牌
     *
     * 参考 DefaultTokenServices 的 revokeToken 方法
     *
     * @param accessToken 刷新令牌
     * @return 访问令牌的信息
     */
    Optional<AuthLoginRespVO> removeAccessToken(String accessToken);

    /**
     * 重置token，用于用户密码变更等，所有登录设备的token都需要失效
     * @param userId 用户id
     */
    void resetToken(Long userId);
}
