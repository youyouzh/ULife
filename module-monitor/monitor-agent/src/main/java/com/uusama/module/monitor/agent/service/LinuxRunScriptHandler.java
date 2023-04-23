package com.uusama.module.monitor.agent.service;

import com.uusama.common.util.FileUtil;
import com.uusama.module.monitor.agent.domain.RunScriptPathContainer;
import com.uusama.module.monitor.agent.exception.AgentDeployException;
import com.uusama.module.monitor.domain.AgentDeployTaskInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Linux平台运行脚本处理器
 * @author zhaohai
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LinuxRunScriptHandler implements RunScriptHandler {

    @Override
    public boolean accept(AgentDeployTaskInfo deployTaskInfo) {
        return SystemUtils.IS_OS_LINUX;
    }

    public String getRunStartCommand() {
        return "sh";
    }

    public RunScriptPathContainer getPathContainer(AgentDeployTaskInfo deployTaskInfo) {
        return RunScriptPathContainer.builder()
            .runStartScriptPath(deployTaskInfo.getScriptDir() + "start.sh")
            .runStopScriptPath(deployTaskInfo.getScriptDir() + "stop.sh")
            .runRestartScriptPath(deployTaskInfo.getScriptDir() + "restart.sh")
            .startScriptTemplatePath("/scripts/linux-start.sh")
            .stopScriptTemplatePath("/scripts/linux-stop.sh")
            .restartScriptTemplatePath("/scripts/linux-restart.sh")
            .build();
    }

    @Override
    public void readyRunScript(AgentDeployTaskInfo deployTaskInfo) {
        FileUtil.mkdirIfNeed(deployTaskInfo.getScriptDir());
        FileUtil.mkdirIfNeed(deployTaskInfo.getLogDir());
        RunScriptPathContainer pathContainer = getPathContainer(deployTaskInfo);
        readyRunScript(deployTaskInfo, pathContainer.getRunStartScriptPath(), pathContainer.getStartScriptTemplatePath());
        readyRunScript(deployTaskInfo, pathContainer.getRunStopScriptPath(), pathContainer.getStopScriptTemplatePath());
        readyRunScript(deployTaskInfo, pathContainer.getRunRestartScriptPath(), pathContainer.getRestartScriptTemplatePath());
    }

    private void readyRunScript(AgentDeployTaskInfo deployTaskInfo, String runScriptPath, String templatePath) {
        // 启动脚本，由于脚本中变量会变更，每次都重写脚本

        // 读取模板脚本文件
        String scriptTemplate;
        try {
            scriptTemplate = IOUtils.resourceToString(templatePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("read linux-start script file failed.", e);
            throw new AgentDeployException("读取脚本文件失败");
        }

        // 替换模板文件中的变量
        for (Map.Entry<String, String> paramEntry : deployTaskInfo.getParamMap().entrySet()) {
            scriptTemplate = scriptTemplate.replace(paramEntry.getKey() + "={}", paramEntry.getKey() + "=" + paramEntry.getValue());
        }

        // 将脚本写入专门文件
        try {
            IOUtils.write(scriptTemplate, Files.newOutputStream(new File(runScriptPath).toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("write script file error. runScriptPath: {}", runScriptPath, e);
            throw new AgentDeployException("初始化脚本文件失败: " + runScriptPath);
        }
    }

    @Override
    public void runScript(AgentDeployTaskInfo deployTaskInfo, String scriptPath) {
        try {
            log.info("runScript: {}", scriptPath);
            Process process = Runtime.getRuntime().exec(getRunStartCommand() + " " + scriptPath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.info("script: " + line);
            }

            boolean exitValue = process.waitFor(1, TimeUnit.MINUTES);
            if (!exitValue) {
                log.error("run script exist code is not error. scriptPath: {}", scriptPath);
                throw new AgentDeployException("脚本运行时异常退出： " + scriptPath);
            }
        } catch (IOException e) {
            log.error("run script exception. scriptPath: {}", scriptPath, e);
            throw new AgentDeployException("脚本运行异常： " + scriptPath);
        } catch (InterruptedException e) {
            log.error("script run interrupted. scriptPath: {}", scriptPath, e);
            throw new AgentDeployException("脚本运行中断： " + scriptPath);
        }
    }
}
