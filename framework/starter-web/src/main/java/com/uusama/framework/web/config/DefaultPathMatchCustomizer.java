package com.uusama.framework.web.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

/**
 * 默认路径匹配器
 * @author uusama
 */
@AutoConfiguration
public class DefaultPathMatchCustomizer implements PathMatchCustomizer {

    @Override
    public void pathMatcher(PathMatchConfigurer configurer) {

    }
}
