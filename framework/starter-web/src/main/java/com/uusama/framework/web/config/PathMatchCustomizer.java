package com.uusama.framework.web.config;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

/**
 * 自定义路径匹配器，方便统一给controller添加路径前缀
 *
 * @author uusama
 */
public interface PathMatchCustomizer {

    /**
     * 可添加路径匹配规则
     * @param configurer 路径配置器
     */
    void pathMatcher(PathMatchConfigurer configurer);

    /**
     * 一个默认的前缀匹配器
     * 示例： prefixPathMatcher(configurer, "/admin-api", "**.controller.admin.**")
     * @param configurer configurer
     * @param prefix 匹配路径前缀
     * @param controllerPackagePattern 匹配控制器包名，支持*模糊匹配
     */
    default void prefixPathMatcher(PathMatchConfigurer configurer, String prefix, String controllerPackagePattern) {
        AntPathMatcher antPathMatcher = new AntPathMatcher(".");
        // 仅仅匹配 controller 包
        configurer.addPathPrefix(prefix, clazz -> clazz.isAnnotationPresent(RestController.class)
            && antPathMatcher.match(controllerPackagePattern, clazz.getPackage().getName()));
    }
}
