package com.uusama.module.monitor.mapper;

import com.uusama.common.util.CollUtil;
import com.uusama.module.monitor.entity.DeployAgentDO;
import com.uusama.module.monitor.entity.DeployTaskDO;

import java.util.Collection;
import java.util.Map;

/**
 * @author zhaohai
 */
public interface DeployAgentMapper extends BaseMapper<DeployAgentDO> {

    /**
     * 根据 deployTasks 查找关联的 DeployAgent
     * @param deployTasks 任务列表
     * @return agentId -> DeployAgent 映射
     */
    default Map<Integer, DeployAgentDO> selectMap(Collection<DeployTaskDO> deployTasks) {
        return selectMapByIds(CollUtil.convertList(deployTasks, DeployTaskDO::getAgentId));
    }
}
