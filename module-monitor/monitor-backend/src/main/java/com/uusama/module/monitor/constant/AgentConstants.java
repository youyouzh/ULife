package com.uusama.module.monitor.constant;

import lombok.experimental.UtilityClass;

/**
 * @author zhaohai
 * agent相关常亮定义
 */
@UtilityClass
public class AgentConstants {
    public static final String SEND_DEPLOY_TASK_API = "http://{agentIp}:8031/api/v1/run-deploy";

    public static final String DEPLOY_LOG_LINE_TEMPLATE = "【{time}】 {log}\n";
}
