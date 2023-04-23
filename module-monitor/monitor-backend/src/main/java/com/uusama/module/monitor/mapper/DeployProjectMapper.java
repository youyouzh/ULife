package com.uusama.module.monitor.mapper;

import com.uusama.common.util.CollUtil;
import com.uusama.module.monitor.entity.DeployProjectDO;
import com.uusama.module.monitor.entity.DeployTaskDO;

import java.util.Collection;
import java.util.Map;

/**
 * @author zhaohai
 */
public interface DeployProjectMapper extends BaseMapper<DeployProjectDO> {

    /**
     * 根据 deployTasks 查找关联的 DeployProject
     * @param deployTasks 任务列表
     * @return projectId -> DeployProject映射
     */
    default Map<Integer, DeployProjectDO> selectMap(Collection<DeployTaskDO> deployTasks) {
        return selectMapByIds(CollUtil.convertList(deployTasks, DeployTaskDO::getProjectId));
    }
}
