package com.uusama.module.system.service.oauth2;

import com.uusama.common.util.CollUtil;
import com.uusama.common.util.DateTimeUtil;
import com.uusama.common.util.IdUtil;
import com.uusama.common.util.StrUtil;
import com.uusama.framework.api.constants.GlobalErrorCodeConstants;
import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.framework.security.LoginUser;
import com.uusama.framework.security.api.UserTokenApi;
import com.uusama.framework.web.enums.UserTypeEnum;
import com.uusama.module.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenPageReqVO;
import com.uusama.module.system.entity.oauth2.OAuth2AccessTokenDO;
import com.uusama.module.system.entity.oauth2.OAuth2ClientDO;
import com.uusama.module.system.entity.oauth2.OAuth2RefreshTokenDO;
import com.uusama.module.system.mapper.oauth2.OAuth2AccessTokenMapper;
import com.uusama.module.system.mapper.oauth2.OAuth2RefreshTokenMapper;
import com.uusama.module.system.redis.OAuth2AccessTokenRedisDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.uusama.common.util.CollUtil.convertSet;
import static com.uusama.framework.web.exception.ServiceExceptionUtil.exception0;

/**
 * OAuth2.0 Token Service 实现类
 *
 * @author uusama
 */
@Service
@RequiredArgsConstructor
public class OAuth2TokenServiceImpl implements OAuth2TokenService, UserTokenApi {
    private final OAuth2AccessTokenMapper oauth2AccessTokenMapper;
    private final OAuth2RefreshTokenMapper oauth2RefreshTokenMapper;
    private final OAuth2AccessTokenRedisDAO oauth2AccessTokenRedisDAO;
    private final OAuth2ClientService oauth2ClientService;

    @Override
    @Transactional
    public OAuth2AccessTokenDO createAccessToken(Long userId, UserTypeEnum userType, String clientId, List<String> scopes) {
        OAuth2ClientDO clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        // 创建刷新令牌
        OAuth2RefreshTokenDO refreshTokenDO = createOAuth2RefreshToken(userId, userType, clientDO, scopes);
        // 创建访问令牌
        return createOAuth2AccessToken(refreshTokenDO, clientDO);
    }

    @Override
    public OAuth2AccessTokenDO refreshAccessToken(String refreshToken, String clientId) {
        // 查询访问令牌
        OAuth2RefreshTokenDO refreshTokenDO = oauth2RefreshTokenMapper.selectByRefreshToken(refreshToken);
        if (refreshTokenDO == null) {
            throw exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "无效的刷新令牌");
        }

        // 校验 Client 匹配
        OAuth2ClientDO clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        if (StrUtil.notEqual(clientId, refreshTokenDO.getClientId())) {
            throw exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "刷新令牌的客户端编号不正确");
        }

        // 移除相关的访问令牌
        List<OAuth2AccessTokenDO> accessTokenDOs = oauth2AccessTokenMapper.selectListByRefreshToken(refreshToken);
        if (CollUtil.isNotEmpty(accessTokenDOs)) {
            oauth2AccessTokenMapper.deleteBatchIds(convertSet(accessTokenDOs, OAuth2AccessTokenDO::getId));
            oauth2AccessTokenRedisDAO.deleteList(convertSet(accessTokenDOs, OAuth2AccessTokenDO::getAccessToken));
        }

        // 已过期的情况下，删除刷新令牌
        if (DateTimeUtil.isExpired(refreshTokenDO.getExpiresTime())) {
            oauth2RefreshTokenMapper.deleteById(refreshTokenDO.getId());
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "刷新令牌已过期");
        }

        // 创建访问令牌
        return createOAuth2AccessToken(refreshTokenDO, clientDO);
    }

    @Override
    public OAuth2AccessTokenDO getAccessToken(String accessToken) {
        // 优先从 Redis 中获取
        OAuth2AccessTokenDO accessTokenDO = oauth2AccessTokenRedisDAO.get(accessToken);
        if (accessTokenDO != null) {
            return accessTokenDO;
        }

        // 获取不到，从 MySQL 中获取
        accessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(accessToken);
        // 如果在 MySQL 存在，则往 Redis 中写入
        if (accessTokenDO != null && !DateTimeUtil.isExpired(accessTokenDO.getExpiresTime())) {
            oauth2AccessTokenRedisDAO.set(accessTokenDO);
        }
        return accessTokenDO;
    }

    @Override
    public OAuth2AccessTokenDO checkAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = getAccessToken(accessToken);
        if (accessTokenDO == null) {
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "访问令牌不存在");
        }
        if (DateTimeUtil.isExpired(accessTokenDO.getExpiresTime())) {
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "访问令牌已过期");
        }
        return accessTokenDO;
    }

    @Override
    public LoginUser checkUserToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = checkAccessToken(accessToken);
        return LoginUser.builder()
            .id(accessTokenDO.getUserId())
            .userType(accessTokenDO.getUserType())
            .build();
    }

    @Override
    public OAuth2AccessTokenDO removeAccessToken(String accessToken) {
        // 删除访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(accessToken);
        if (accessTokenDO == null) {
            return null;
        }
        oauth2AccessTokenMapper.deleteById(accessTokenDO.getId());
        oauth2AccessTokenRedisDAO.delete(accessToken);
        // 删除刷新令牌
        oauth2RefreshTokenMapper.deleteByRefreshToken(accessTokenDO.getRefreshToken());
        return accessTokenDO;
    }

    @Override
    public PageResult<OAuth2AccessTokenDO> getAccessTokenPage(OAuth2AccessTokenPageReqVO reqVO) {
        return oauth2AccessTokenMapper.selectPage(reqVO);
    }

    private OAuth2AccessTokenDO createOAuth2AccessToken(OAuth2RefreshTokenDO refreshTokenDO, OAuth2ClientDO clientDO) {
        OAuth2AccessTokenDO accessTokenDO = OAuth2AccessTokenDO.builder().accessToken(generateAccessToken())
            .userId(refreshTokenDO.getUserId()).userType(refreshTokenDO.getUserType())
            .clientId(clientDO.getClientId()).scopes(refreshTokenDO.getScopes())
            .refreshToken(refreshTokenDO.getRefreshToken())
            .expiresTime(LocalDateTime.now().plusSeconds(clientDO.getAccessTokenValiditySeconds())).build();
        // accessTokenDO.setTenantId(TenantContextHolder.getTenantId()); // 手动设置租户编号，避免缓存到 Redis 的时候，无对应的租户编号
        oauth2AccessTokenMapper.insert(accessTokenDO);
        // 记录到 Redis 中
        oauth2AccessTokenRedisDAO.set(accessTokenDO);
        return accessTokenDO;
    }

    private OAuth2RefreshTokenDO createOAuth2RefreshToken(Long userId, UserTypeEnum userType, OAuth2ClientDO clientDO, List<String> scopes) {
        OAuth2RefreshTokenDO refreshToken = OAuth2RefreshTokenDO.builder()
            .refreshToken(generateRefreshToken())
            .userId(userId).userType(userType)
            .clientId(clientDO.getClientId()).scopes(scopes)
            .expiresTime(LocalDateTime.now().plusSeconds(clientDO.getRefreshTokenValiditySeconds()))
            .build();
        oauth2RefreshTokenMapper.insert(refreshToken);
        return refreshToken;
    }

    private static String generateAccessToken() {
        return IdUtil.fastSimpleUUID();
    }

    private static String generateRefreshToken() {
        return IdUtil.fastSimpleUUID();
    }
}
