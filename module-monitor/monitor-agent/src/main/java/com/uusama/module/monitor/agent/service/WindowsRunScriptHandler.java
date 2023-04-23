package com.uusama.module.monitor.agent.service;

import com.uusama.module.monitor.agent.domain.RunScriptPathContainer;
import com.uusama.module.monitor.agent.exception.AgentDeployException;
import com.uusama.module.monitor.domain.AgentDeployTaskInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Windows平台脚本运行处理
 * @author zhaohai
 */
@Slf4j
@Component
public class WindowsRunScriptHandler extends LinuxRunScriptHandler {
    private ThreadPoolExecutor cmdStreamPool;

    @PostConstruct
    public void init() {
        cmdStreamPool = new ThreadPoolExecutor(
            5,
            10,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(100),
            new CustomizableThreadFactory("cmdStreamPool"),
            new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public boolean accept(AgentDeployTaskInfo deployTaskInfo) {
        return SystemUtils.IS_OS_WINDOWS;
    }

    @Override
    public String getRunStartCommand() {
        return "cmd.exe start";
    }

    public RunScriptPathContainer getPathContainer(AgentDeployTaskInfo deployTaskInfo) {
        return RunScriptPathContainer.builder()
            .runStartScriptPath(deployTaskInfo.getScriptDir() + "start.bat")
            .runStopScriptPath(deployTaskInfo.getScriptDir() + "stop.bat")
            .runRestartScriptPath(deployTaskInfo.getScriptDir() + "restart.bat")
            .startScriptTemplatePath("/scripts/win-start.bat")
            .stopScriptTemplatePath("/scripts/win-stop.bat")
            .restartScriptTemplatePath("/scripts/win-restart.bat")
            .build();
    }


    @Override
    @SneakyThrows
    public void runScript(AgentDeployTaskInfo deployTaskInfo, String scriptPath) {
        log.info("run java script: {}", scriptPath);
        // windows运行bat脚本有问题
    }

    public void tryRunScript(AgentDeployTaskInfo deployTaskInfo, String scriptPath) {
        // 尝试运行，windows平台运行bat脚本会卡住，暂时不知道怎么解决
        try {
            Process process = Runtime.getRuntime().exec(getRunStartCommand() + " " + scriptPath);
            cmdStreamPool.execute(new CmdStreamThread(process.getInputStream()));
            cmdStreamPool.execute(new CmdStreamThread(process.getErrorStream()));

            boolean exitValue = process.waitFor(1, TimeUnit.MINUTES);
            if (!exitValue) {
                log.error("run script exist code is not error. scriptPath: {}", scriptPath);
                throw new AgentDeployException("脚本运行时异常退出： " + scriptPath);
            }
            process.destroy();
        } catch (IOException e) {
            log.error("run script exception. scriptPath: {}", scriptPath, e);
            throw new AgentDeployException("脚本运行异常： " + scriptPath);
        } catch (InterruptedException e) {
            log.error("script run interrupted. scriptPath: {}", scriptPath, e);
            throw new AgentDeployException("脚本运行中断： " + scriptPath);
        }
    }

    private String getRunCmdResult(String command) {
        try {
            Process process = Runtime.getRuntime().exec(getRunStartCommand() + "" + command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            // Read the output from the command
            StringBuilder result = null;
            String line;
            while ((line = stdInput.readLine()) != null) {
                log.info("script: " + line);
                result.append(line);
            }
            process.destroy();
            return result.toString();
        } catch (IOException e) {
            log.error("run script exception. command: {}", command, e);
            throw new AgentDeployException("脚本运行异常： " + command);
        }
    }

    private static class CmdStreamThread implements Runnable {
        private final InputStream inputStream;

        public CmdStreamThread(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    log.info("script: " + line);
                }
            } catch (IOException e) {
                log.error("cmd stream IOException.", e);
            }
        }
    }
}
