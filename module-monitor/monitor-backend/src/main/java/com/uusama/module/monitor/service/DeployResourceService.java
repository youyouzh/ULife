package com.uusama.module.monitor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uusama.module.monitor.entity.DeployAgentDO;
import com.uusama.module.monitor.entity.DeployAppDO;
import com.uusama.module.monitor.entity.DeployProjectDO;
import com.uusama.module.monitor.entity.DeployProjectVersionDO;
import com.uusama.module.monitor.mapper.DeployAgentMapper;
import com.uusama.module.monitor.mapper.DeployAppMapper;
import com.uusama.module.monitor.mapper.DeployProjectMapper;
import com.uusama.module.monitor.mapper.DeployProjectVersionMapper;
import com.uusama.module.monitor.pojo.AmisCrudData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author zhaohai
 * 部署相关资源处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeployResourceService {
    private final DeployAppMapper deployAppDao;
    private final DeployAgentMapper deployAgentDao;
    private final DeployProjectMapper deployProjectDao;
    private final DeployProjectVersionMapper deployProjectVersionDao;

    public AmisCrudData<DeployAppDO> queryApps(String keyword, Page<DeployAppDO> pageWrapper) {
        LambdaQueryWrapper<DeployAppDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(keyword), DeployAppDO::getCode, keyword)
            .like(StringUtils.isNotBlank(keyword), DeployAppDO::getName, keyword)
            .orderByAsc(DeployAppDO::getCode);
        return AmisCrudData.of(deployAppDao.selectPage(pageWrapper, queryWrapper));
    }

    public AmisCrudData<DeployAgentDO> queryAgents(String keyword, Page<DeployAgentDO> pageWrapper) {
        LambdaQueryWrapper<DeployAgentDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(keyword), DeployAgentDO::getName, keyword)
            .orderByAsc(DeployAgentDO::getName);
        return AmisCrudData.of(deployAgentDao.selectPage(pageWrapper, queryWrapper));
    }

    public AmisCrudData<DeployProjectDO> queryProjects(String keyword, Page<DeployProjectDO> pageWrapper) {
        LambdaQueryWrapper<DeployProjectDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(keyword), DeployProjectDO::getName, keyword)
            .orderByDesc(DeployProjectDO::getId);
        return AmisCrudData.of(deployProjectDao.selectPage(pageWrapper, queryWrapper));
    }

    public AmisCrudData<DeployProjectVersionDO> queryProjectVersions(Integer projectId, Page<DeployProjectVersionDO> pageWrapper) {
        LambdaQueryWrapper<DeployProjectVersionDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(projectId), DeployProjectVersionDO::getProjectId, projectId)
            .orderByDesc(DeployProjectVersionDO::getId);
        return AmisCrudData.of(deployProjectVersionDao.selectPage(pageWrapper, queryWrapper));
    }
}
