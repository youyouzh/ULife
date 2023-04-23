package com.uusama.module.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.netflix.appinfo.InstanceInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author zhaohai
 * 部署的代理，每个服务器上会有一台代理，执行部署任务
 */
@TableName("deploy_agent")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeployAgentDO extends BaseDO {

    /**
     * 名称
     */
    private String name;

    /**
     * agent的ip地址
     */
    private String ip;

    /**
     * 访问token
     */
    private String token;

    /**
     * 关于机器的描述信息，补充硬件配置等
     */
    private String description;

    private InstanceInfo.InstanceStatus eurekaState;

    /**
     * 上一次心跳时间
     */
    private LocalDateTime lastHeartbeatTime;

    /**
     * 上一次活动时间，除了心跳，上报部署时间也会更新该时间
     */
    private LocalDateTime lastActivityTime;

    /**
     * 当前正在部署的任务列表，json字符串
     */
    private String deployTaskIds;

    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static DeployAgentDO createWithIp(String ip) {
        return DeployAgentDO.builder().name(ip).ip(ip)
            .description("Eureka自动注册创建，请及时更新相关信息")
            .lastActivityTime(LocalDateTime.now()).build();
    }

    public void heartbeat() {
        lastHeartbeatTime = LocalDateTime.now();
        lastActivityTime = LocalDateTime.now();
    }

    /**
     * 是否活动中
     * @return true表示活动中
     */
    public boolean isActive() {
        return Objects.nonNull(lastHeartbeatTime) && lastHeartbeatTime.isAfter(LocalDateTime.now().minusSeconds(10))
            || Objects.nonNull(lastActivityTime) && lastActivityTime.isAfter(LocalDateTime.now().minusSeconds(10));
    }

    @Override
    public LambdaQueryWrapper<DeployAgentDO> getUniqueQuery() {
        return new LambdaQueryWrapper<DeployAgentDO>().eq(StringUtils.isNotBlank(ip), DeployAgentDO::getIp, ip);
    }
}
