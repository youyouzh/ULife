package com.uusama.module.monitor.exception;

/**
 * 业务级异常，批量任务解析异常
 * @author zhaohai
 */
public class BatchTaskParseException extends RuntimeException {

    public BatchTaskParseException(String message) {
        super(message);
    }
}
