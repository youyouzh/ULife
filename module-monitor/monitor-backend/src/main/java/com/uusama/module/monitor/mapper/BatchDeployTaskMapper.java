package com.uusama.module.monitor.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uusama.module.monitor.entity.BatchDeployTaskDO;

import java.time.LocalDateTime;
import java.util.List;

public interface BatchDeployTaskMapper extends BaseMapper<BatchDeployTaskDO> {

    /**
     * 查询最近的部署任务
     * @return List of DeployTask
     */
    default List<BatchDeployTaskDO> selectRecentTasks() {
        return this.selectList(new LambdaQueryWrapper<BatchDeployTaskDO>()
                                   .between(true, BatchDeployTaskDO::getDeployStartTime,
                                            LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)));
    }

}
