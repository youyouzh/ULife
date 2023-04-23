package com.uusama.module.monitor.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author zhaohai
 * 为agent定制的部署任务信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentDeployTaskInfo {
    @Schema(description = "任务id")
    private int taskId;

    @Schema(description = "项目id")
    private int projectId;

    @Schema(description = "项目编码")
    private String projectCode;

    @Schema(description = "部署根目录")
    private String deployRootPath;

    @Schema(description = "日志根目录")
    private String logRootPath;

    @Schema(description = "参数列表")
    private Map<String, String> params;

    @Schema(description = "agentId")
    private int agentId;

    @Schema(description = "版本")
    private String version;

    @Schema(description = "部署包下载地址")
    private String deployFileUrl;

    @Schema(description = "部署包扩展名，后缀")
    private String deployFileExtension;

    public String wrapperPath(String path) {
        return path.replace("\\", File.separator).replace("/", File.separator);
    }

    public String getPackageSavePath() {
        return wrapperPath(this.deployRootPath) + File.separator + this.projectCode + File.separator
            + this.projectCode + "-" + this.version + "." + this.deployFileExtension;
    }

    public String getScriptDir() {
        return wrapperPath(this.deployRootPath) + File.separator + this.projectCode + File.separator
            + "bin" + File.separator;
    }

    public String getLogDir() {
        return wrapperPath(this.logRootPath) + File.separator
            + this.projectCode + File.separator + "logs";
    }

    public String getServiceLogPath() {
        return getLogDir() + File.separator + "service.log";
    }

    public Map<String, String> getParamMap() {
        HashMap<String, String> fullParamMap = new HashMap<>(10);
        fullParamMap.put("SERVICE_NAME", this.projectCode);
        fullParamMap.put("SERVICE_HOME", wrapperPath(this.deployRootPath) + File.separator + this.projectCode);
        fullParamMap.put("PACKAGE_PATH", getPackageSavePath());
        fullParamMap.put("SERVICE_LOG_PATH", getServiceLogPath());
        // GC LOG PATH
        Optional.ofNullable(params).ifPresent(fullParamMap::putAll);
        fullParamMap.putAll(params);
        return fullParamMap;
    }
}
