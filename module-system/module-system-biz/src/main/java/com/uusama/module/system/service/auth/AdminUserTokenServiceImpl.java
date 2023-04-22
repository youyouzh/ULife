package com.uusama.module.system.service.auth;

import com.uusama.common.util.DateTimeUtil;
import com.uusama.common.util.IdUtil;
import com.uusama.framework.api.constants.GlobalErrorCodeConstants;
import com.uusama.framework.mybatis.query.LambdaQueryWrapperX;
import com.uusama.framework.web.util.ParamUtils;
import com.uusama.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.uusama.module.system.entity.user.AdminUserTokenDO;
import com.uusama.module.system.mapper.user.AdminUserTokenMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Auth Service 实现类
 * 一个破后台，没啥访问量，就不用redis存token了
 *
 * @author uusama
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminUserTokenServiceImpl implements UserTokenService {
    private static final int ACCESS_TOKEN_EXPIRE_HOURS = 24;
    private static final int REFRESH_TOKEN_EXPIRE_DAYS = 30;
    private final AdminUserTokenMapper adminUserTokenMapper;

    @Override
    public AuthLoginRespVO createAccessToken(Long userId, String clientId) {
        AdminUserTokenDO adminUserToken = AdminUserTokenDO.builder()
            .userId(userId)
            .clientId(clientId)
            .accessToken(createToken())
            .refreshToken(createToken())
            .accessTokenExpireTime(LocalDateTime.now().plusHours(ACCESS_TOKEN_EXPIRE_HOURS))
            .refreshTokenExpireTime(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRE_DAYS))
            .build();
        adminUserTokenMapper.insert(adminUserToken);
        return AuthLoginRespVO.of(adminUserToken);
    }

    @Override
    public AuthLoginRespVO refreshAccessToken(String refreshToken, String clientId) {
        AdminUserTokenDO adminUserTokenDO = adminUserTokenMapper.selectByRefreshTokenAndClientId(refreshToken, clientId);

        // 检查刷新令牌是否存在
        ParamUtils.checkNotNull(adminUserTokenDO, GlobalErrorCodeConstants.UNAUTHORIZED, "刷新令牌不存在");
        // 检查刷新令牌是否过期
        ParamUtils.checkMatch(DateTimeUtil.isNotExpired(adminUserTokenDO.getRefreshTokenExpireTime()), GlobalErrorCodeConstants.UNAUTHORIZED, "刷新令牌已过期，请重新登录");

        // 更新访问令牌
        adminUserTokenDO.setAccessToken(createToken());
        adminUserTokenDO.setAccessTokenExpireTime(LocalDateTime.now().plusSeconds(ACCESS_TOKEN_EXPIRE_HOURS));
        adminUserTokenMapper.updateById(adminUserTokenDO);
        return AuthLoginRespVO.of(adminUserTokenDO);
    }

    @Override
    public AuthLoginRespVO getAccessToken(String accessToken) {
        AdminUserTokenDO adminUserTokenDO = adminUserTokenMapper.selectOne(AdminUserTokenDO::getAccessToken, accessToken);
        ParamUtils.checkNotNull(adminUserTokenDO, GlobalErrorCodeConstants.UNAUTHORIZED, "访问令牌不存在");
        ParamUtils.checkMatch(DateTimeUtil.isNotExpired(adminUserTokenDO.getAccessTokenExpireTime()), GlobalErrorCodeConstants.UNAUTHORIZED, "访问令牌已过期");
        return AuthLoginRespVO.of(adminUserTokenDO);
    }

    @Override
    public AuthLoginRespVO checkAccessToken(String accessToken) {
        AdminUserTokenDO adminUserTokenDO = adminUserTokenMapper.selectOne(AdminUserTokenDO::getAccessToken, accessToken);
        ParamUtils.checkNotNull(adminUserTokenDO, GlobalErrorCodeConstants.UNAUTHORIZED, "访问令牌不存在");
        return AuthLoginRespVO.of(adminUserTokenDO);
    }

    @Override
    public Optional<AuthLoginRespVO> removeAccessToken(String accessToken) {
        AdminUserTokenDO adminUserTokenDO = adminUserTokenMapper.selectOne(AdminUserTokenDO::getAccessToken, accessToken);
        if (adminUserTokenDO == null) {
            log.warn("The accessToken is not exist. accessToken: {}", accessToken);
            return Optional.empty();
        }
        adminUserTokenMapper.deleteById(adminUserTokenDO);
        return Optional.of(AuthLoginRespVO.of(adminUserTokenDO));
    }

    @Override
    public void resetToken(Long userId) {
        log.info("resetToken. userId: {}", userId);
        adminUserTokenMapper.delete(new LambdaQueryWrapperX<AdminUserTokenDO>().eq(AdminUserTokenDO::getUserId, userId));
    }

    private String createToken() {
        return IdUtil.fastSimpleUUID();
    }
}