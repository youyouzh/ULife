package com.uusama.module.monitor.agent.controller;

import com.uusama.common.util.FileUtil;
import com.uusama.framework.api.pojo.CommonResult;
import com.uusama.module.monitor.agent.service.DeployScheduledJobService;
import com.uusama.module.monitor.domain.AgentDeployTaskInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaohai
 * 部署agent主控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AgentController {
    private final DeployScheduledJobService deployScheduledJobService;

    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> result = new HashMap<>(6);
        result.put("a", "a");
        return result;
    }

    @PostMapping("/api/v1/run-deploy")
    public CommonResult<Object> deploy(@RequestBody AgentDeployTaskInfo deployTaskInfo) {
        deployScheduledJobService.asyncRunDeploy(deployTaskInfo);
        return CommonResult.success();
    }

    @GetMapping("/api/v1/read-logs")
    public CommonResult<Object> readLogs(@RequestParam("logPath") String logPath,
                                         @RequestParam(value = "lineCount", required = false, defaultValue = "10") int lineCount) {
        return FileUtil.readLastNLine(logPath, lineCount)
            .<CommonResult<Object>>map(CommonResult::success)
            .orElseGet(() -> CommonResult.error("读取文件内容失败"));
    }

    @GetMapping("/api/v1/download-logs")
    public void downloadLogs() {

    }

    @PostMapping("/api/v1/exec-scripts")
    public void execScripts() {

    }
}
