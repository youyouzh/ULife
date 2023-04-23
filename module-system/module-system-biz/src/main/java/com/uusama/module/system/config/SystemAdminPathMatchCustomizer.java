package com.uusama.module.system.config;

import com.uusama.framework.web.customizer.PathMatchCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

/**
 * 统一给system菜单controller添加路由前缀
 *
 * @author uusama
 */
@Component
public class SystemAdminPathMatchCustomizer implements PathMatchCustomizer {

    @Override
    public void pathMatcher(PathMatchConfigurer configurer) {
        prefixPathMatcher(configurer, "/admin-api", "**.system.controller.admin.**");
    }

}
