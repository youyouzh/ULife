package com.uusama.module.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.uusama.module.monitor.domain.DeployEventInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author zhaohai
 */
@TableName("deploy_event")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeployEventDO extends BaseDO {
    private int taskId;

    private String message;

    private LocalDateTime eventTime;

    public DeployEventDO(DeployEventInfo deployEventInfo) {
        this.taskId = deployEventInfo.getTaskId();
        this.message = deployEventInfo.getMessage();
        this.eventTime = deployEventInfo.getEventTime();
    }
}
