package com.uusama.framework.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * @author uusama
 */
@ConfigurationProperties(prefix = "uusama.security")
@Validated
@Data
public class SecurityProperties {

    /** HTTP 请求时，访问令牌的请求 Header */
    @NotEmpty(message = "Token Header 不能为空")
    private String tokenHeader = "Authorization";

    /** mock 模式的开关 */
    @NotNull(message = "mock 模式的开关不能为空")
    private Boolean mockEnable = false;

    /** 放开所有请求接口，并设置默认用户 */
    private Long useUserId = 1L;

    /**
     * mock 模式的密钥
     * 一定要配置密钥，保证安全性
     * 这里设置了一个默认值，因为实际上只有 mockEnable 为 true 时才需要配置。
     */
    @NotEmpty(message = "mock 模式的密钥不能为空")
    private String mockSecret = "test";

    /** 免登录的 URL 列表 */
    private List<String> permitAllUrls = Collections.emptyList();

}
