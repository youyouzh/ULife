package com.uusama.module.monitor.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaohai
 * 部署项目信息，外部用
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeployProjectInfo {
    private int projectId;
}
