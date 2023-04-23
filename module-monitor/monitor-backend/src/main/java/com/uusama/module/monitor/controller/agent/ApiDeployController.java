package com.uusama.module.monitor.controller.agent;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uusama.framework.api.pojo.CommonResult;
import com.uusama.module.monitor.domain.DeployEventInfo;
import com.uusama.module.monitor.entity.DeployAgentDO;
import com.uusama.module.monitor.entity.DeployEventDO;
import com.uusama.module.monitor.entity.DeployTaskDO;
import com.uusama.module.monitor.mapper.DeployAgentMapper;
import com.uusama.module.monitor.mapper.DeployEventMapper;
import com.uusama.module.monitor.mapper.DeployTaskMapper;
import com.uusama.module.monitor.service.DeployRecordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @author zhaohai
 * 部署相关的api接口
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiDeployController {
    private final DeployAgentMapper deployAgentDao;
    private final DeployTaskMapper deployTaskDao;
    private final DeployEventMapper deployEventDao;
    private final DeployRecordService deployRecordService;

    @GetMapping("/v1/deploy-events")
    public CommonResult<List<DeployEventDO>> getDeployEvents() {
        return CommonResult.success(deployEventDao.selectList(null));
    }

    @Operation(summary = "上报部署事件")
    @PostMapping("/v1/deploy-events")
    public CommonResult<Object> reportEvents(@RequestBody DeployEventInfo deployEventInfo) {
        DeployTaskDO deployTask = deployTaskDao.selectById(deployEventInfo.getTaskId());
        if (Objects.isNull(deployTask)) {
            log.error("The deployTask is not exist: {}", deployEventInfo.getTaskId());
            return CommonResult.error("部署id错误");
        }
        deployRecordService.recordEvents(deployEventInfo, deployTask);
        return CommonResult.success();
    }

    @Operation(summary = "Agent心跳上报接口")
    @GetMapping("/v1/agents/heartbeat")
    public CommonResult<Object> agentHeartbeat(@RequestParam("agentIp") String agentIp) {
        val queryWrapper = new LambdaQueryWrapper<DeployAgentDO>()
            .eq(StringUtils.isNotBlank(agentIp), DeployAgentDO::getIp, agentIp);
        DeployAgentDO deployAgent = deployAgentDao.selectOne(queryWrapper);
        if (Objects.isNull(deployAgent)) {
            // 收到心跳先不创建，eureka注册才创建默认
            log.error("There are not any agent for this ip: {}", agentIp);
            return CommonResult.error("部署agent未找到");
        }
        deployAgent.heartbeat();
        deployAgentDao.updateById(deployAgent);
        return CommonResult.success();
    }

}
