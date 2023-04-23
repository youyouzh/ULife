package com.uusama.framework.security.customizer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

/**
 * @author uusama
 */
@AutoConfiguration
public class DefaultAuthorizeRequestsCustomizer extends AuthorizeRequestsCustomizer {

    @Override
    public void customize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry.antMatchers("/test").permitAll();
    }

}
