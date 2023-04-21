package com.uusama.framework.web.properties;

import com.uusama.framework.web.config.WebAutoConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

/**
 * web配置，暂时未使用
 *
 * @author uusama
 */
@Data
public class WebProperties {
    private Api appApi = new Api("/app-api", "**.controller.app.**");
    private Api adminApi = new Api("/admin-api", "**.controller.admin.**");
    private Ui adminUi;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Api {

        /**
         * API 前缀，实现所有 Controller 提供的 RESTFul API 的统一前缀
         * <p>
         * <p>
         * 意义：通过该前缀，避免 Swagger、Actuator 意外通过 Nginx 暴露出来给外部，带来安全性问题
         * 这样，Nginx 只需要配置转发到 /api/* 的所有接口即可。
         *
         * @see WebAutoConfiguration#configurePathMatch(PathMatchConfigurer)
         */
        @Schema(description = "API 前缀")
        private String prefix;

        /**
         * Controller 所在包的 Ant 路径规则
         * <p>
         * 主要目的是，给该 Controller 设置指定的 {@link #prefix}
         */
        @Schema(description = "Controller 所在包")
        private String controller;

    }

    @Data
    public static class Ui {

        /**
         * 访问地址
         */
        private String url;

    }

    public void configurePathMatch(PathMatchConfigurer configurer, WebProperties.Api api) {
        AntPathMatcher antPathMatcher = new AntPathMatcher(".");
        // 仅仅匹配 controller 包
        configurer.addPathPrefix(api.getPrefix(), clazz -> clazz.isAnnotationPresent(RestController.class)
            && antPathMatcher.match(api.getController(), clazz.getPackage().getName()));
    }
}
