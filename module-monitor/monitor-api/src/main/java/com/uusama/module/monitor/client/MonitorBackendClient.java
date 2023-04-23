package com.uusama.module.monitor.client;

import com.uusama.framework.api.pojo.CommonResult;
import com.uusama.module.monitor.domain.DeployEventInfo;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhaohai
 */
@FeignClient(value = "monitor-backend", contextId = "MonitorBackendClient")
public interface MonitorBackendClient {

    /**
     * 上报agent心跳
     * @param agentIp agentIp地址
     * @return Result<Object>
     */
    @GetMapping("/api/v1/agents/heartbeat")
    CommonResult<Object> agentHeartbeat(@RequestParam("agentIp") String agentIp);

    /**
     * 下载文件
     * @param url 文件地址
     * @return Response
     */
    @GetMapping("/api/v1/files/download")
    Response downloadFile(@RequestParam("url") String url);

    /**
     * 上报部署事件
     * @param deployEventInfo 部署事件信息
     * @return Result
     */
    @PostMapping("/api/v1/deploy-events")
    CommonResult<Object> reportEvents(@RequestBody DeployEventInfo deployEventInfo);
}
