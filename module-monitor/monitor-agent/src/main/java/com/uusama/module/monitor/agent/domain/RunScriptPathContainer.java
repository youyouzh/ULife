package com.uusama.module.monitor.agent.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 脚本路径容器
 * @author zhaohai
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunScriptPathContainer {
    private String runStartScriptPath;
    private String runStopScriptPath;
    private String runRestartScriptPath;

    private String startScriptTemplatePath;
    private String stopScriptTemplatePath;
    private String restartScriptTemplatePath;
}
