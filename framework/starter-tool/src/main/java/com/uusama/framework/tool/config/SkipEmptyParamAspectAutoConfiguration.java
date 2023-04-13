package com.uusama.framework.tool.config;

import com.uusama.framework.tool.aspect.SkipEmptyParamAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkipEmptyParamAspectAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SkipEmptyParamAspect skipEmptyParamAspect() {
        return new SkipEmptyParamAspect();
    }
}
