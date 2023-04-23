package com.uusama.module.monitor.mapper;

import com.uusama.common.util.CollUtil;
import com.uusama.module.monitor.entity.DeployProjectVersionDO;
import com.uusama.module.monitor.entity.DeployTaskDO;

import java.util.Collection;
import java.util.Map;

/**
 * @author zhaohai
 */
public interface DeployProjectVersionMapper extends BaseMapper<DeployProjectVersionDO> {

    /**
     * 根据 deployTasks 查找关联的 DeployProjectVersion
     * @param deployTasks 任务列表
     * @return projectId -> DeployProjectVersion 映射
     */
    default Map<Integer, DeployProjectVersionDO> selectMap(Collection<DeployTaskDO> deployTasks) {
        return selectMapByIds(CollUtil.convertList(deployTasks, DeployTaskDO::getProjectVersionId));
    }
}
