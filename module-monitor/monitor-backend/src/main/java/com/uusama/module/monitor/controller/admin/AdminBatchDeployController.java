package com.uusama.module.monitor.controller.admin;

import com.uusama.common.util.CollUtil;
import com.uusama.framework.api.pojo.CommonResult;
import com.uusama.framework.web.util.JsonUtils;
import com.uusama.module.monitor.entity.BaseDO;
import com.uusama.module.monitor.entity.BatchDeployTaskDO;
import com.uusama.module.monitor.entity.DeployTaskDO;
import com.uusama.module.monitor.entity.SysFileDO;
import com.uusama.module.monitor.exception.BatchTaskParseException;
import com.uusama.module.monitor.mapper.BatchDeployTaskMapper;
import com.uusama.module.monitor.mapper.DeployTaskMapper;
import com.uusama.module.monitor.pojo.BatchDeployTaskRequest;
import com.uusama.module.monitor.service.BatchDeployTaskService;
import com.uusama.module.monitor.service.SysFileService;
import com.uusama.module.monitor.util.DbUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaohai
 * 批量部署相关控制处理
 */
@Slf4j
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminBatchDeployController {
    private final DeployTaskMapper deployTaskDao;
    private final BatchDeployTaskMapper batchDeployTaskDao;
    private final BatchDeployTaskService batchDeployTaskService;
    private final SysFileService sysFileService;

    @Operation(summary = "获取批量部署任务列表")
    @GetMapping("/batch-deploy-tasks")
    public CommonResult<Object> getBatchDeployTask(@RequestParam(required = false) String keyword,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int perPage) {
        return CommonResult.success(batchDeployTaskService.queryBatchTasks(keyword, DbUtils.pageWrapper(page, perPage)));
    }

    @Operation(summary = "新增批量部署任务")
    @PostMapping("/batch-deploy-tasks")
    public CommonResult<Object> insertBatchDeployTasks(@RequestBody BatchDeployTaskRequest request) {
        BatchDeployTaskDO batchDeployTask = request.wrapBatchTask();

        // 创建批量任务和具体的任务列表
        batchDeployTaskDao.insert(batchDeployTask);
        List<DeployTaskDO> deployTasks = request.wrapTasks();
        deployTasks.forEach(v -> v.setBatchTaskId(batchDeployTask.getId()));
        deployTasks.forEach(deployTaskDao::insert);

        // 写入任务id信息
        batchDeployTask.setDeployTaskIds(JsonUtils.serialize(CollUtil.convertList(deployTasks, BaseDO::getId)));
        batchDeployTaskDao.updateById(batchDeployTask);

        // 如果不是约定时间，立刻触发
        if (batchDeployTask.attachDeployingTime()) {
            batchDeployTaskService.runBatchDeployTask(batchDeployTask);
        }
        return CommonResult.success();
    }

    @Operation(summary = "导入批量部署任务描述json")
    @PostMapping("/import-batch-deploy-tasks")
    public CommonResult<Object> importBatchDeployTasks(@RequestBody BatchDeployTaskRequest request) {
        try {
            String unzipPathRoot = sysFileService.getFullSaveFilePath(request.getZipUrl());
            return CommonResult.success(batchDeployTaskService.saveBatchDeployTask(request, unzipPathRoot));
        } catch (BatchTaskParseException e) {
            return CommonResult.error(e.getMessage());
        }
    }

    @Operation(summary = "上传批量部署任务压缩文件")
    @PostMapping("/upload-batch-deploy-tasks")
    public CommonResult<Object> uploadBatchDeployTasks(@RequestPart("file") MultipartFile multipartFile) {
        SysFileDO sysFile;
        try {
            sysFile = sysFileService.uploadFile(multipartFile, "batchDeploy");
        } catch (IOException e) {
            return CommonResult.error(e.getMessage());
        }

        Map<String, String> resultData = new HashMap<>(4);
        resultData.put("url", sysFile.getUrl());
        try {
            String zipPath = sysFileService.getFullSaveFilePath(sysFile.getUrl());
            BatchDeployTaskRequest parseRequest = batchDeployTaskService.parseBatchTaskFileResult(zipPath);
            parseRequest.setZipUrl(sysFile.getUrl());
            resultData.put("batchTaskJson", JsonUtils.serialize(parseRequest));
            return CommonResult.success(resultData);
        } catch (BatchTaskParseException e) {
            return CommonResult.error(e.getMessage());
        }
    }

    @Operation(summary = "修改批量部署项目任务")
    @PutMapping("/batch-deploy-tasks")
    public CommonResult<Object> updateBatchDeployTask(@RequestBody BatchDeployTaskDO batchDeployTask) {
        batchDeployTaskDao.updateById(batchDeployTask);
        return CommonResult.success();
    }

    @Operation(summary = "执行批量部署任务，部署其下所有项目")
    @PostMapping("/run-batch-deploy-tasks/{batchTaskId}")
    public CommonResult<Object> runBatchDeployTask(@PathVariable("batchTaskId") int batchTaskId) {
        BatchDeployTaskDO batchDeployTask = batchDeployTaskDao.selectById(batchTaskId);
        if (!batchDeployTask.canReDeploy()) {
            log.info("the deploy task is deploying, can not deploy again. batchTaskId: {}", batchTaskId);
            return CommonResult.error("该项目已在部署中");
        }

        batchDeployTaskService.runBatchDeployTask(batchDeployTask);
        return CommonResult.success();
    }
}
