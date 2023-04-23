package com.uusama.module.monitor.controller.admin;

import com.uusama.common.util.DateTimeUtil;
import com.uusama.framework.api.pojo.CommonResult;
import com.uusama.module.monitor.domain.DeployState;
import com.uusama.module.monitor.entity.DeployAgentDO;
import com.uusama.module.monitor.entity.DeployTaskDO;
import com.uusama.module.monitor.mapper.DeployAgentMapper;
import com.uusama.module.monitor.mapper.DeployTaskMapper;
import com.uusama.module.monitor.service.DeployTaskService;
import com.uusama.module.monitor.util.DbUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhaohai
 * 部署相关控制处理
 */
@Slf4j
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminDeployController {
    private final DeployTaskMapper deployTaskDao;
    private final DeployAgentMapper deployAgentDao;
    private final DeployTaskService deployTaskService;

    @Operation(summary = "获取部署任务列表")
    @GetMapping("/deploy-tasks")
    public CommonResult<Object> getDeployTasks(@RequestParam(required = false) Integer projectId,
                                               @RequestParam(required = false) Integer agentId,
                                               @RequestParam(required = false) Integer batchTaskId,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int perPage) {
        return CommonResult.success(deployTaskService.queryTasks(projectId, agentId, batchTaskId, DbUtils.pageWrapper(page, perPage)));
    }

    @Operation(summary = "新增部署任务")
    @PostMapping("/deploy-tasks")
    public CommonResult<Object> insertDeployTasks(@RequestBody DeployTaskDO deployTask) {
        deployTask.initDeployStateAndTime(deployTask.getDeployStartTime());
        deployTaskDao.insert(deployTask);
        if (deployTask.getDeployState() == DeployState.INIT) {
            deployTaskService.runDeployTask(deployTask);
        }
        return CommonResult.success();
    }

    @Operation(summary = "新增某个部署版本关联的部署任务")
    @PostMapping("/project-versions/deploy-tasks")
    public CommonResult<Object> insertProjectVersionDeployTasks(@RequestParam("projectId") int projectId,
                                                                @RequestParam("projectVersionId") int projectVersionId,
                                                                @RequestParam(value = "agentId", required = false) String agentId,
                                                                @RequestParam("deployStartTime") @DateTimeFormat(pattern = DateTimeUtil.DATE_TIME_PATTERN) LocalDateTime deployStartTime,
                                                                @RequestParam(value = "deployAll", required = false) boolean deployAll) {
        if (deployAll) {
            // 全量部署
            return CommonResult.error("全量部署暂不支持，敬请期待！");
        }
        if (StringUtils.isBlank(agentId)) {
            return CommonResult.error("请选择需要部署的Agent");
        }
        for (String strAgentId : agentId.split(",")) {
            int intAgentId = Integer.parseInt(strAgentId);
            // 检查agent是否存在
            DeployAgentDO deployAgent = deployAgentDao.selectById(intAgentId);
            if (Objects.isNull(deployAgent)) {
                log.error("invalid agentId: {}", intAgentId);
                return CommonResult.error("选择无效的Agent，agentId: " + intAgentId);
            }

            // 创建deployTask，有可能创建不成功
            Optional<DeployTaskDO> deployTask = deployTaskService.createDeployTask(projectId, projectVersionId, intAgentId, deployStartTime);
            if (!deployTask.isPresent()) {
                log.info("There are deploying tasks. projectId: {}, agentId: {}", projectId, intAgentId);
                return CommonResult.error("选中的Agent已经有部署任务，请重新选择： " + deployAgent.getName());
            }

            // 对于立即部署的任务，直接出发
            if (deployTask.get().getDeployState() == DeployState.INIT) {
                deployTaskService.runDeployTask(deployTask.get());
            }
        }
        return CommonResult.success();
    }

    @Operation(summary = "修改部署任务")
    @PutMapping("/deploy-tasks")
    public CommonResult<Object> updateDeployTask(@RequestBody DeployTaskDO deployTask) {
        deployTaskDao.updateById(deployTask);
        return CommonResult.success();
    }

    @Operation(summary = "执行项目部署")
    @PutMapping("/run-deploy-tasks/{taskId}")
    public CommonResult<Object> runDeployTask(@PathVariable("taskId") int taskId) {
        DeployTaskDO deployTask = deployTaskDao.selectById(taskId);
        if (!deployTask.canReDeploy()) {
            log.info("the deploy task is deploying, can not deploy again. taskId: {}", taskId);
            return CommonResult.error("该项目已在部署中");
        }

        // 重置部署任务
        deployTask.resetDeploy();
        deployTaskDao.updateById(deployTask);

        if (!deployTaskService.runDeployTask(deployTask)) {
            // 失败则返回错误
            return CommonResult.error(deployTask.getRemark());
        }
        return CommonResult.success();
    }
}
