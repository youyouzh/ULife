package com.uusama.module.monitor.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author zhaohai
 * 项目部署在哪些机器上，用于批量部署等
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeployProjectToAgentDO extends BaseDO {
    private int projectId;
    private int agentId;

    @EnumValue
    private RelateType relateType;

    /**
     * 关联关系
     */
    public enum RelateType {
        /**
         * 预设
         */
        DEFAULT,
        /**
         * 部署时关联
         */
        DEPLOY,
        ;
    }
}
