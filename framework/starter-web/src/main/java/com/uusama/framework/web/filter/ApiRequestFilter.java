package com.uusama.framework.web.filter;

import com.uusama.framework.web.properties.WebProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * 过滤 /admin-api、/app-api 等 API 请求的过滤器
 *
 * @author uusama
 */
@RequiredArgsConstructor
public abstract class ApiRequestFilter extends OncePerRequestFilter {

    protected final WebProperties webProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 只过滤 API 请求的地址
        return !StringUtils.startsWithAny(request.getRequestURI(), webProperties.getAdminApi().getPrefix(),
                                         webProperties.getAppApi().getPrefix());
    }

}
