package com.uusama.module.system.service.auth;

import com.uusama.common.util.IdUtil;
import com.uusama.framework.api.constants.GlobalErrorCodeConstants;
import com.uusama.framework.web.util.ParamUtils;
import com.uusama.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.uusama.module.system.entity.user.AdminUserTokenDO;
import com.uusama.module.system.mapper.user.AdminUserMapper;
import com.uusama.module.system.mapper.user.AdminUserTokenMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Auth Service 实现类
 *
 * @author uusama
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminUserTokenServiceImpl implements UserTokenService {
    private static final int ACCESS_TOKEN_EXPIRE_HOURS = 24;
    private static final int REFRESH_TOKEN_EXPIRE_DAYS = 30;

    private final AdminUserMapper adminUserMapper;
    private final AdminUserTokenMapper adminUserTokenMapper;

    @Override
    public AuthLoginRespVO createAccessToken(Long userId, String clientId) {
        AdminUserTokenDO adminUserToken = AdminUserTokenDO.builder()
            .userId(userId)
            .clientId(clientId)
            .accessToken(IdUtil.fastSimpleUUID())
            .refreshToken(IdUtil.fastSimpleUUID())
            .accessTokenExpireTime(LocalDateTime.now().plusHours(ACCESS_TOKEN_EXPIRE_HOURS))
            .refreshTokenExpireTime(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRE_DAYS))
            .build();
        adminUserTokenMapper.insert(adminUserToken);
        return AuthLoginRespVO.of(adminUserToken);
    }

    @Override
    public AuthLoginRespVO refreshAccessToken(String refreshToken, String clientId) {
        return null;
    }

    @Override
    public AuthLoginRespVO getAccessToken(String accessToken) {
        return null;
    }

    @Override
    public AuthLoginRespVO checkAccessToken(String accessToken) {
        AdminUserTokenDO adminUserTokenDO = adminUserTokenMapper.selectOne(AdminUserTokenDO::getAccessToken, accessToken);
        ParamUtils.checkNotNull(adminUserTokenDO, GlobalErrorCodeConstants.UNAUTHORIZED);
        return AuthLoginRespVO.of(adminUserTokenDO);
    }

    @Override
    public AuthLoginRespVO removeAccessToken(String accessToken) {
        return null;
    }
}