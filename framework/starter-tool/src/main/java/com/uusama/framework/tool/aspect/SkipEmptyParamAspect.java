package com.uusama.framework.tool.aspect;

import com.google.common.collect.ImmutableMap;
import com.uusama.framework.tool.annotation.SkipEmpty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Aspect
public class SkipEmptyParamAspect {
    private static final Map<Class<?>, Object> RETURN_TYPE_OBJECT_MAP = ImmutableMap.of(
        List.class, Collections.emptyList(),
        Set.class, Collections.emptySet(),
        Collection.class, Collections.emptyList(),
        Map.class, Collections.emptyMap(),
        Optional.class, Optional.empty()
    );

    @Around("@annotation(com.uusama.framework.tool.annotation.SkipEmpty)")
    public Object checkParam(ProceedingJoinPoint pjp) throws Throwable {
        // SkipEmpty @Target=ElementType.METHOD，直接强转是安全的
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();

        // 返回值参数类型检查
        Class<?> returnType = methodSignature.getReturnType();
        if (!RETURN_TYPE_OBJECT_MAP.containsKey(returnType)) {
            return pjp.proceed();
        }

        // 获取不需要检查的参数
        SkipEmpty skipEmpty = methodSignature.getMethod().getAnnotation(SkipEmpty.class);
        List<String> ignoreParams = Stream.of(skipEmpty.ignoreParams()).collect(Collectors.toList());

        // 检查 collection 或者 map 参数列表是否为空
        Parameter[] parameters = methodSignature.getMethod().getParameters();
        Object[] args = pjp.getArgs();
        for (int index = 0; index < args.length; index++) {
            if (ignoreParams.contains(parameters[index].getName())) {
                // 不检查标记的参数
                continue;
            }

            if (Collection.class.isAssignableFrom(parameters[index].getType()) && ObjectUtils.isEmpty(args[index])) {
                return RETURN_TYPE_OBJECT_MAP.get(returnType);
            }
            if (Map.class.isAssignableFrom(parameters[index].getType()) && ObjectUtils.isEmpty(args[index])) {
                return RETURN_TYPE_OBJECT_MAP.get(returnType);
            }
        }

        return pjp.proceed();
    }
}
