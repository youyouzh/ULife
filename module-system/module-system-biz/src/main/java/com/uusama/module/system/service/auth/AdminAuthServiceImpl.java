package com.uusama.module.system.service.auth;

import com.uusama.framework.web.enums.UserTypeEnum;
import com.uusama.framework.web.util.ServletUtils;
import com.uusama.framework.web.util.TracerUtils;
import com.uusama.module.system.constant.OAuth2ClientConstants;
import com.uusama.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.uusama.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.uusama.module.system.controller.admin.auth.vo.AuthSocialLoginReqVO;
import com.uusama.module.system.entity.logger.LoginLogDO;
import com.uusama.module.system.entity.user.AdminUserDO;
import com.uusama.module.system.logger.LoginLogTypeEnum;
import com.uusama.module.system.logger.LoginResultEnum;
import com.uusama.module.system.mapper.logger.LoginLogMapper;
import com.uusama.module.system.mapper.user.AdminUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.uusama.framework.web.exception.ServiceExceptionUtil.exception;
import static com.uusama.framework.web.util.ServletUtils.getClientIP;
import static com.uusama.module.system.constant.ErrorCodeConstants.AUTH_LOGIN_BAD_CREDENTIALS;
import static com.uusama.module.system.constant.ErrorCodeConstants.AUTH_LOGIN_USER_DISABLED;
import static com.uusama.module.system.constant.ErrorCodeConstants.USER_NOT_EXISTS;

/**
 * Auth Service 实现类
 *
 * @author uusama
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {
    private final AdminUserMapper adminUserMapper;
    private final LoginLogMapper loginLogMapper;
    private final PasswordEncoder passwordEncoder;
    private final AdminUserTokenServiceImpl adminUserTokenService;

    /**
     * 验证账号 + 密码。如果通过，则返回用户
     *
     * @param username 账号
     * @param password 密码
     * @return 用户
     */
    public AdminUserDO authenticate(String username, String password) {
        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_USERNAME;
        // 校验账号是否存在
        AdminUserDO user = adminUserMapper.selectByUsername(username);
        if (user == null) {
            createLoginLog(null, username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // 校验是否禁用
        if (user.getState().isDisable()) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
        return user;
    }

    /**
     * 账号登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    public AuthLoginRespVO login(@Valid AuthLoginReqVO reqVO) {
        // 校验验证码
        // validateCaptcha(reqVO);

        // 使用账号密码，进行登录
        AdminUserDO user = authenticate(reqVO.getUsername(), reqVO.getPassword());

        // 如果 socialType 非空，说明需要绑定社交用户
        if (reqVO.getSocialType() != null) {
//            socialUserService.bindSocialUser(new SocialUserBindReqDTO(user.getId(), getUserType().getValue(),
//                    reqVO.getSocialType(), reqVO.getSocialCode(), reqVO.getSocialState()));
        }
        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getId(), reqVO.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME);
    }

    private void createLoginLog(Long userId, String username,
                                LoginLogTypeEnum logTypeEnum, LoginResultEnum loginResult) {
        // 插入登录日志
        LoginLogDO loginLog = LoginLogDO.builder()
            .logType(logTypeEnum)
            .userId(userId)
            .userType(getUserType())
            .username(username)
            .userAgent(ServletUtils.getUserAgent())
            .userIp(getClientIP())
            .result(loginResult)
            .traceId(TracerUtils.getTraceId())
            .build();
        loginLogMapper.insert(loginLog);
        // 更新最后登录时间
        if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
            adminUserMapper.updateById(AdminUserDO.builder().id(userId).loginIp(getClientIP()).loginDate(LocalDateTime.now()).build());
        }
    }

    private AuthLoginRespVO createTokenAfterLoginSuccess(Long userId, String username, LoginLogTypeEnum logType) {
        // 插入登陆日志
        createLoginLog(userId, username, logType, LoginResultEnum.SUCCESS);
        // 创建访问令牌
        return adminUserTokenService.createAccessToken(userId, OAuth2ClientConstants.CLIENT_ID_DEFAULT);
    }

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 登录结果
     */
    public AuthLoginRespVO refreshToken(String refreshToken) {
        return adminUserTokenService.refreshAccessToken(refreshToken, OAuth2ClientConstants.CLIENT_ID_DEFAULT);
    }

    /**
     * 基于 token 退出登录
     *
     * @param token token
     * @param logType 登出类型
     */
    public void logout(String token, LoginLogTypeEnum logType) {
        // 删除访问令牌
        Optional<AuthLoginRespVO> authLoginRespVO = adminUserTokenService.removeAccessToken(token);
        // 删除成功，则记录登出日志
        authLoginRespVO.ifPresent(v -> createLogoutLog(v.getUserId(), getUserType(), logType));
    }

    private void createLogoutLog(Long userId, UserTypeEnum userType, LoginLogTypeEnum logType) {
        LoginLogDO loginLog = LoginLogDO.builder()
            .logType(logType)
            .userId(userId)
            .userType(userType)
            .username(getUserType() == userType ? getUsername(userId).orElse(null) : "")
            .traceId(TracerUtils.getTraceId())
            .userAgent(ServletUtils.getUserAgent())
            .userIp(getClientIP())
            .result(LoginResultEnum.SUCCESS)
            .build();
        loginLogMapper.insert(loginLog);
    }

    private Optional<String> getUsername(Long userId) {
        return Optional.ofNullable(userId)
            .map(adminUserMapper::selectById)
            .map(AdminUserDO::getUsername);
    }

    private UserTypeEnum getUserType() {
        return UserTypeEnum.ADMIN;
    }

}