package com.uusama.module.monitor.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uusama.module.monitor.domain.DeployState;
import com.uusama.module.monitor.entity.DeployTaskDO;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhaohai
 */
public interface DeployTaskMapper extends BaseMapper<DeployTaskDO> {

    /**
     * 是否存在部署中的任务，避免创建多个重复任务
     * @param projectId 项目ID
     * @param agentId Agent ID
     * @return true表示存在部署中任务
     */
    default boolean existDeployingTask(int projectId, int agentId) {
        return this.exists(new LambdaQueryWrapper<DeployTaskDO>()
                               .eq(DeployTaskDO::getProjectId, projectId)
                               .eq(DeployTaskDO::getAgentId, agentId)
                               .in(DeployTaskDO::getDeployState, Arrays.asList(DeployState.DEPLOYING, DeployState.INIT)));
    }

    /**
     * 查询最近的部署任务
     * @return List of DeployTask
     */
    default List<DeployTaskDO> selectRecentTasks() {
        return this.selectList(new LambdaQueryWrapper<DeployTaskDO>()
           .between(DeployTaskDO::getDeployStartTime, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)));
    }

}
