package com.uusama.framework.security.filter;

import com.uusama.framework.security.LoginUser;
import com.uusama.framework.security.api.UserTokenApi;
import com.uusama.framework.security.config.SecurityProperties;
import com.uusama.framework.security.util.SecurityAuthUtils;
import com.uusama.framework.web.enums.UserTypeEnum;
import com.uusama.framework.web.exception.GlobalExceptionHandler;
import com.uusama.framework.web.exception.ServiceException;
import com.uusama.framework.api.pojo.CommonResult;
import com.uusama.framework.web.util.ServletUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Token 过滤器，验证 token 的有效性
 * 验证通过后，获得 {@link LoginUser} 信息，并加入到 Spring Security 上下文
 *
 * @author uusama
 */
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final SecurityProperties securityProperties;
    private final GlobalExceptionHandler globalExceptionHandler;
    private final UserTokenApi userTokenApi;

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Optional<String> token = SecurityAuthUtils.obtainAuthorization(request, securityProperties.getTokenHeader());
        if (token.isPresent()) {
            UserTypeEnum userType = UserTypeEnum.ADMIN;
            try {
                // 1.1 基于 token 构建登录用户
                LoginUser loginUser = buildLoginUserByToken(token.get(), userType);
                // 1.2 模拟 Login 功能，方便日常开发调试
                if (loginUser == null) {
                    loginUser = mockLoginUser(request, token.get(), userType);
                }

                // 2. 设置当前用户
                if (loginUser != null) {
                    SecurityAuthUtils.setLoginUser(loginUser, request);
                }
            } catch (Throwable ex) {
                CommonResult<?> result = globalExceptionHandler.allExceptionHandler(request, ex);
                ServletUtils.writeJSON(response, result);
                return;
            }
        }

        // 继续过滤链
        chain.doFilter(request, response);
    }

    private LoginUser buildLoginUserByToken(String token, UserTypeEnum userType) {
        Assert.notNull(userTokenApi, "请实现并注册OAuth2TokenApi接口进行登录权限判断");
        try {
            LoginUser loginUser = userTokenApi.checkUserToken(token);
            if (loginUser == null) {
                return null;
            }
            // 用户类型不匹配，无权限
            if (loginUser.getUserType() != userType) {
                throw new AccessDeniedException("错误的用户类型");
            }
            // 构建登录用户
            return loginUser;
        } catch (ServiceException serviceException) {
            // 校验 Token 不通过时，考虑到一些接口是无需登录的，所以直接返回 null 即可
            return null;
        }
    }

    /**
     * 模拟登录用户，方便日常开发调试
     * 注意，在线上环境下，一定要关闭该功能！！！
     *
     * @param request 请求
     * @param token 模拟的 token，格式为 {@link SecurityProperties#getMockSecret()} + 用户编号
     * @param userType 用户类型
     * @return 模拟的 LoginUser
     */
    private LoginUser mockLoginUser(HttpServletRequest request, String token, UserTypeEnum userType) {
        if (!securityProperties.getMockEnable()) {
            return null;
        }
        // 统一设置默认用户，即使没有token，方便测试
        if (Objects.nonNull(securityProperties.getUseUserId())) {
            return LoginUser.builder().id(securityProperties.getUseUserId()).userType(UserTypeEnum.ADMIN).build();
        }

        // 必须以 mockSecret 开头
        if (!token.startsWith(securityProperties.getMockSecret())) {
            return null;
        }
        // 构建模拟用户
        Long userId = Long.valueOf(token.substring(securityProperties.getMockSecret().length()));
        return LoginUser.builder().id(userId).userType(userType).build();
    }

}
