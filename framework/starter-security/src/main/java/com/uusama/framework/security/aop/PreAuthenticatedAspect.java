package com.uusama.framework.security.aop;

import com.uusama.framework.security.annotations.PreAuthenticated;
import com.uusama.framework.security.util.SecurityFrameworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import static com.uusama.framework.api.constants.GlobalErrorCodeConstants.UNAUTHORIZED;
import static com.uusama.framework.web.exception.ServiceExceptionUtil.exception;

/**
 * 登录检查
 *
 * @author uusama
 */
@Aspect
@Slf4j
public class PreAuthenticatedAspect {

    @Around("@annotation(preAuthenticated)")
    public Object around(ProceedingJoinPoint joinPoint, PreAuthenticated preAuthenticated) throws Throwable {
        if (SecurityFrameworkUtils.getLoginUser() == null) {
            throw exception(UNAUTHORIZED);
        }
        return joinPoint.proceed();
    }

}
