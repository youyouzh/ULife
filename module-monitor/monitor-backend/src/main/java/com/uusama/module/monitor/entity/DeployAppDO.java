package com.uusama.module.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author zhaohai
 * 部署的应用列表，一个应用可能包含多个微服务（DeoployProject)
 */
@TableName("deploy_app")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeployAppDO extends BaseDO {

    /**
     * 唯一编码，为ASCII字符
     */
    private String code;

    /**
     * 具体应用名称
     */
    private String name;

    /**
     * 应用描述信息
     */
    private String description;

    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
