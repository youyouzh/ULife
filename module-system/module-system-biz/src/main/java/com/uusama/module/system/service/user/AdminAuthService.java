package com.uusama.module.system.service.user;

import com.uusama.framework.web.enums.CommonState;
import com.uusama.framework.web.enums.UserTypeEnum;
import com.uusama.framework.web.util.ServletUtils;
import com.uusama.framework.web.util.TracerUtils;
import com.uusama.module.system.constant.OAuth2ClientConstants;
import com.uusama.module.system.controller.admin.vo.auth.AuthLoginReqVO;
import com.uusama.module.system.controller.admin.vo.auth.AuthLoginRespVO;
import com.uusama.module.system.controller.admin.vo.auth.AuthSmsLoginReqVO;
import com.uusama.module.system.controller.admin.vo.auth.AuthSmsSendReqVO;
import com.uusama.module.system.controller.admin.vo.auth.AuthSocialLoginReqVO;
import com.uusama.module.system.convert.user.AuthConvert;
import com.uusama.module.system.entity.user.AdminUserDO;
import com.uusama.module.system.entity.logger.LoginLogDO;
import com.uusama.module.system.entity.oauth2.OAuth2AccessTokenDO;
import com.uusama.module.system.enums.SmsSceneEnum;
import com.uusama.module.system.logger.LoginLogTypeEnum;
import com.uusama.module.system.logger.LoginResultEnum;
import com.uusama.module.system.mapper.auth.AdminUserMapper;
import com.uusama.module.system.mapper.logger.LoginLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.uusama.framework.web.exception.ServiceExceptionUtil.exception;
import static com.uusama.module.system.constant.ErrorCodeConstants.AUTH_LOGIN_BAD_CREDENTIALS;
import static com.uusama.module.system.constant.ErrorCodeConstants.AUTH_LOGIN_USER_DISABLED;
import static com.uusama.module.system.constant.ErrorCodeConstants.AUTH_MOBILE_NOT_EXISTS;
import static com.uusama.module.system.constant.ErrorCodeConstants.AUTH_THIRD_LOGIN_NOT_BIND;
import static com.uusama.module.system.constant.ErrorCodeConstants.USER_NOT_EXISTS;

/**
 * Auth Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthService {
    private final AdminUserMapper adminUserMapper;
    private final LoginLogMapper loginLogMapper;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2TokenService oauth2TokenService;

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
        if (CommonState.isEnable(user.getState())) {
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

    /**
     * 短信验证码发送
     *
     * @param reqVO 发送请求
     */
    public void sendSmsCode(AuthSmsSendReqVO reqVO) {
        // 登录场景，验证是否存在
        if (adminUserMapper.selectByMobile(reqVO.getMobile()) == null) {
            throw exception(AUTH_MOBILE_NOT_EXISTS);
        }
        // 发送验证码
        smsCodeApi.sendSmsCode(AuthConvert.INSTANCE.convert(reqVO).setCreateIp(ServletUtils.getClientIP()));
    }

    /**
         * 短信登录
         *
         * @param reqVO 登录信息
         * @return 登录结果
         */
    public AuthLoginRespVO smsLogin(AuthSmsLoginReqVO reqVO) {
        // 校验验证码
        smsCodeApi.useSmsCode(AuthConvert.INSTANCE.convert(reqVO, SmsSceneEnum.ADMIN_MEMBER_LOGIN.getScene(), getClientIP()));

        // 获得用户信息
        AdminUserDO user = adminUserMapper.selectByMobile(reqVO.getMobile());
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getId(), reqVO.getMobile(), LoginLogTypeEnum.LOGIN_MOBILE);
    }

    private void createLoginLog(Long userId, String username,
                                LoginLogTypeEnum logTypeEnum, LoginResultEnum loginResult) {
        // 插入登录日志
        LoginLogDO loginLog = LoginLogDO.builder()
            .logType(logTypeEnum.getType())
            .userId(userId)
            .userType(getUserType())
            .username(username)
            .userAgent(ServletUtils.getUserAgent())
            .userIp(ServletUtils.getClientIP())
            .result(loginResult.getResult())
            .traceId(TracerUtils.getTraceId())
            .build();
        loginLogMapper.insert(loginLog);
        // 更新最后登录时间
        if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
            adminUserMapper.updateById(AdminUserDO.builder().id(userId).loginIp(ServletUtils.getClientIP()).loginDate(LocalDateTime.now()).build());
        }
    }

    /**
         * 社交快捷登录，使用 code 授权码
         *
         * @param reqVO 登录信息
         * @return 登录结果
         */
    public AuthLoginRespVO socialLogin(@javax.validation.Valid AuthSocialLoginReqVO reqVO) {
        // 使用 code 授权码，进行登录。然后，获得到绑定的用户编号
        Long userId = socialUserService.getBindUserId(UserTypeEnum.ADMIN.getValue(), reqVO.getType(),
                reqVO.getCode(), reqVO.getState());
        if (userId == null) {
            throw exception(AUTH_THIRD_LOGIN_NOT_BIND);
        }

        // 获得用户
        AdminUserDO user = adminUserMapper.selectById(userId);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getId(), user.getUsername(), LoginLogTypeEnum.LOGIN_SOCIAL);
    }

    private AuthLoginRespVO createTokenAfterLoginSuccess(Long userId, String username, LoginLogTypeEnum logType) {
        // 插入登陆日志
        createLoginLog(userId, username, logType, LoginResultEnum.SUCCESS);
        // 创建访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(userId, getUserType().getValue(),
                                                                                 OAuth2ClientConstants.CLIENT_ID_DEFAULT, null);
        // 构建返回结果
        return AuthConvert.INSTANCE.convert(accessTokenDO);
    }

    /**
         * 刷新访问令牌
         *
         * @param refreshToken 刷新令牌
         * @return 登录结果
         */
    public AuthLoginRespVO refreshToken(String refreshToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.refreshAccessToken(refreshToken, OAuth2ClientConstants.CLIENT_ID_DEFAULT);
        return AuthConvert.INSTANCE.convert(accessTokenDO);
    }

    /**
         * 基于 token 退出登录
         *
         * @param token token
         * @param logType 登出类型
         */
    public void logout(String token, Integer logType) {
        // 删除访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.removeAccessToken(token);
        if (accessTokenDO == null) {
            return;
        }
        // 删除成功，则记录登出日志
        createLogoutLog(accessTokenDO.getUserId(), accessTokenDO.getUserType(), logType);
    }

    private void createLogoutLog(Long userId, UserTypeEnum userType, Integer logType) {
        LoginLogDO loginLog = LoginLogDO.builder()
            .logType(logType)
            .userId(userId)
            .userType(userType)
            .username(getUserType() == userType ? getUsername(userId).orElse(null) : "")
            .traceId(TracerUtils.getTraceId())
            .userAgent(ServletUtils.getUserAgent())
            .userIp(ServletUtils.getClientIP())
            .result(LoginResultEnum.SUCCESS.getResult())
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
