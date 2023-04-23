package com.uusama.module.monitor.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uusama.framework.web.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaohai
 * 部署的项目，和jar包或者代码仓库一一对应
 */
@TableName("deploy_project")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeployProjectDO extends BaseDO {

    /**
     * 项目的唯一编码
     */
    private String code;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 所属应用ID
     */
    private Integer appId;

    /**
     * 项目描述，说明项目是干啥的，实现什么功能等
     */
    private String description;

    /**
     * 部署启动服务占用的端口号
     */
    private String servicePort;

    /**
     * 项目状态
     */
    @EnumValue
    private ProjectState projectState;

    /**
     * 部署时的项目根路径
     */
    private String deployRootPath;

    /**
     * 项目日志根目录
     */
    private String logRootPath;

    /**
     * 预设部署的AgentId列表，缺省部署机器
     */
    private String deployAgentIds;

    /**
     * 是否需要注册到Eureka
     */
//    private boolean registerEureka;

    /**
     * 项目专属参数列表，一个HashMap
     */
    private String params;

    /**
     * 配置文件类型
     */
    @EnumValue
    private ConfigFileType configFileType;

    /**
     * 配置文件名称
     */
    private String configFilename;

    /**
     * 配置文件内容
     */
    private String configFileContent;

    /**
     * 当前线上版本
     */
    private Integer onlineProjectVersionId;

    /**
     * 上一次部署时间
     */
    private LocalDateTime lastDeployStartTime;
    private LocalDateTime lastDeployEndTime;

    // 部署平台或者容器： Windows，Linux，部署命令不一样

    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public enum ConfigFileType {
        /**
         * YAML
         */
        YAML,
        /**
         * PROPERTIES
         */
        PROPERTIES,
        ;
    }

    public enum ProjectState {
        /**
         * 线上运行中
         */
        ONLINE,

        /**
         * 暂时下线
         */
        OFFLINE,

        /**
         * 废弃，删除，不再使用
         */
        DISCARD,
        ;

        public boolean canNotDeploy() {
            return this == DISCARD;
        }
    }

    public Map<String, String> getParamMap() {
        if (StringUtils.isBlank(params)) {
            return new HashMap<>(0);
        }
        return JsonUtils.deserialize(params, Map.class);
    }

    @Override
    public LambdaQueryWrapper<DeployProjectDO> getUniqueQuery() {
        return new LambdaQueryWrapper<DeployProjectDO>()
            .eq(org.apache.commons.lang.StringUtils.isNotBlank(getCode()), DeployProjectDO::getCode, this.getCode());
    }
}
