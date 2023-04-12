package com.uusama.framework.recorder.api;

import com.uusama.framework.recorder.pojo.OperateLog;

/**
 * @author uusama
 */
public interface OperateLogApi {

    /**
     * 创建操作日志
     *
     * @param operateLog 请求
     */
    void createOperateLog(OperateLog operateLog);
}
