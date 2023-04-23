package com.uusama.module.monitor.service;

import com.uusama.module.monitor.mapper.DeployEventMapper;
import com.uusama.module.monitor.mapper.DeployProjectMapper;
import com.uusama.module.monitor.mapper.DeployTaskMapper;
import com.uusama.module.monitor.domain.DeployEventInfo;
import com.uusama.module.monitor.domain.DeployState;
import com.uusama.module.monitor.entity.DeployEventDO;
import com.uusama.module.monitor.entity.DeployProjectDO;
import com.uusama.module.monitor.entity.DeployTaskDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author zhaohai
 * 部署相关资源处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeployRecordService {
    private final DeployEventMapper deployEventDao;
    private final DeployTaskMapper deployTaskDao;
    private final DeployProjectMapper deployProjectDao;

    public void recordEvents(DeployEventInfo deployEventInfo, DeployTaskDO deployTask) {
        deployEventDao.insert(new DeployEventDO(deployEventInfo));

        // 将信息更新到部署任务
        deployTask.setDeployState(deployEventInfo.getDeployState());
        deployTask.setRemark(deployTask.getRemark());
        deployTask.appendDeployLog(deployEventInfo.getMessage());

        // 如果部署完成，则更新部署时长等信息
        if (deployEventInfo.getDeployState() == DeployState.SUCCESS) {
            deployTask.setDeployEndTime(LocalDateTime.now());
            finishProjectDeploy(deployTask);
        }
        deployTaskDao.updateById(deployTask);
    }

    private void finishProjectDeploy(DeployTaskDO deployTask) {
        DeployProjectDO deployProject = deployProjectDao.selectById(deployTask.getProjectId());
        deployProject.setLastDeployStartTime(deployTask.getDeployStartTime());
        deployProject.setLastDeployEndTime(deployTask.getDeployEndTime());
        deployProject.setOnlineProjectVersionId(deployTask.getProjectVersionId());
        deployProjectDao.updateById(deployProject);
    }
}
