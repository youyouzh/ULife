package com.uusama.framework.security.util;

import com.uusama.framework.security.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;

/**
 * 安全服务工具类
 *
 * @author uusama
 */
@Slf4j
public class SecurityAuthUtils {

    public static final String AUTHORIZATION_BEARER = "Bearer";
    private static final String REQUEST_ATTRIBUTE_LOGIN_USER = "login_user";

    private SecurityAuthUtils() {}

    /**
     * 从请求中，获得认证 Token
     *
     * @param request 请求
     * @param header 认证 Token 对应的 Header 名字
     * @return 认证 Token
     */
    public static Optional<String> obtainAuthorization(HttpServletRequest request, String header) {
        String authorization = request.getHeader(header);
        if (!StringUtils.hasText(authorization)) {
            return Optional.empty();
        }
        authorization = authorization.trim();
        if (!StringUtils.startsWithIgnoreCase(authorization, AUTHORIZATION_BEARER)) {
            log.info("unknown authorization: {}", authorization);
            return Optional.empty();
        }
        return Optional.of(authorization.substring(AUTHORIZATION_BEARER.length() + 1));
    }

    /**
     * 获取当前用户
     *
     * @return 当前用户
     */
    public static Optional<LoginUser> getLoginUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(v -> v.getPrincipal() instanceof LoginUser)
            .map(v -> (LoginUser) v.getPrincipal());
    }

    /**
     * 获得当前用户的编号，从上下文中
     *
     * @return 用户编号
     */
    public static Long getLoginUserId() {
        return getLoginUser().map(LoginUser::getId).orElse(null);
    }

    /**
     * 设置当前用户
     *
     * @param loginUser 登录用户
     * @param request 请求
     */
    public static void setLoginUser(LoginUser loginUser, HttpServletRequest request) {
        // 创建 Authentication，并设置到上下文
        Authentication authentication = buildAuthentication(loginUser, request);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 额外设置到 request 中，用于 ApiAccessLogFilter 可以获取到用户编号；
        // 原因是，Spring Security 的 Filter 在 ApiAccessLogFilter 后面，在它记录访问日志时，线上上下文已经没有用户编号等信息
        request.setAttribute(REQUEST_ATTRIBUTE_LOGIN_USER, loginUser);
    }

    private static Authentication buildAuthentication(LoginUser loginUser, HttpServletRequest request) {
        // 创建 UsernamePasswordAuthenticationToken 对象
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginUser, null, Collections.emptyList());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authenticationToken;
    }

}
