package com.uusama.module.system.entity.logger;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.uusama.framework.recorder.enums.OperateTypeEnum;
import com.uusama.framework.api.pojo.CommonResult;
import com.uusama.module.system.entity.user.BaseUserDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 操作日志表
 *
 * @author uusama
 */
@TableName(value = "system_operate_log", autoResultMap = true)
@KeySequence("system_operate_log_seq")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OperateLogDO extends BaseUserDO {

    /** {@link #javaMethodArgs} 的最大长度 */
    public static final Integer JAVA_METHOD_ARGS_MAX_LENGTH = 8000;

    /** {@link #resultData} 的最大长度 */
    public static final Integer RESULT_MAX_LENGTH = 4000;

    /**
     * 链路追踪编号
     * 一般来说，通过链路追踪编号，可以将访问日志，错误日志，链路追踪日志，logger 打印日志等，结合在一起，从而进行排错。
     */
    private String traceId;
    /** 操作模块 */
    private String module;
    /** 操作名 */
    private String name;
    /** 操作分类 */
    @EnumValue
    private OperateTypeEnum type;
    /**
     * 操作内容，记录整个操作的明细
     * 例如说，修改编号为 1 的用户信息，将性别从男改成女，将姓名从uusama改成源码。
     */
    private String content;
    /**
     * 拓展字段，有些复杂的业务，需要记录一些字段
     * 例如说，记录订单编号，则可以添加 key 为 "orderId"，value 为订单编号
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> exts;

    /** 请求方法名 */
    private String requestMethod;
    /** 请求地址 */
    private String requestUrl;
    /** 用户 IP */
    private String userIp;
    /** 浏览器 UA */
    private String userAgent;

    /** Java 方法名 */
    private String javaMethod;
    /**
     * Java 方法的参数
     *
     * 实际格式为 Map<String, Object>
     *     不使用 @TableField(typeHandler = FastjsonTypeHandler.class) 注解的原因是，数据库存储有长度限制，会进行裁剪，会导致 JSON 反序列化失败
     *     其中，key 为参数名，value 为参数值
     */
    private String javaMethodArgs;
    /** 开始时间 */
    private LocalDateTime startTime;
    /** 执行时长，单位：毫秒 */
    private Integer duration;
    /**
     * 结果码
     *
     * 目前使用的 {@link CommonResult#getCode()} 属性
     */
    private Integer resultCode;
    /**
     * 结果提示
     *
     * 目前使用的 {@link CommonResult#getMsg()} 属性
     */
    private String resultMsg;
    /**
     * 结果数据
     *
     * 如果是对象，则使用 JSON 格式化
     */
    private String resultData;

}
