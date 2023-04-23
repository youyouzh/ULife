package com.uusama.module.monitor.agent.service;

import com.uusama.module.monitor.agent.domain.RunScriptPathContainer;
import com.uusama.module.monitor.domain.AgentDeployTaskInfo;

/**
 * @author zhaohai
 */
public interface RunScriptHandler {

    /**
     * 该处理器能是否能处理
     * @param deployTaskInfo task
     * @return true表示能处理
     */
    boolean accept(AgentDeployTaskInfo deployTaskInfo);

    /**
     * 获取脚本启动应用
     * @return 启动命令
     */
    String getRunStartCommand();

    /**
     * 获取脚本运行路径容器
     * @param deployTaskInfo task
     * @return RunScriptPathContainer
     */
    RunScriptPathContainer getPathContainer(AgentDeployTaskInfo deployTaskInfo);

    /**
     * 处理器方法
     * @param deployTaskInfo task
     */
    void readyRunScript(AgentDeployTaskInfo deployTaskInfo);

    /**
     * 运行脚本
     * @param deployTaskInfo task
     * @param scriptPath 脚本绝对路径
     */
    void runScript(AgentDeployTaskInfo deployTaskInfo, String scriptPath);

    /**
     * 运行启动脚本
     * @param deployTaskInfo task
     */
    default void runStart(AgentDeployTaskInfo deployTaskInfo) {
        runScript(deployTaskInfo, getPathContainer(deployTaskInfo).getRunStartScriptPath());
    }

    /**
     * 运行停止脚本
     * @param deployTaskInfo task
     */
    default void runStop(AgentDeployTaskInfo deployTaskInfo) {
        runScript(deployTaskInfo, getPathContainer(deployTaskInfo).getRunStopScriptPath());
    }
}
