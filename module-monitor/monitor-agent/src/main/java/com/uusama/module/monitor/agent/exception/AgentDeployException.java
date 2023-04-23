package com.uusama.module.monitor.agent.exception;

/**
 * 自定义部署异常
 * @author zhaohai
 */
public class AgentDeployException extends RuntimeException {

    public AgentDeployException(String message) {
        super(message);
    }

}
