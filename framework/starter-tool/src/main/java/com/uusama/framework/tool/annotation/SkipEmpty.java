package com.uusama.framework.tool.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对于根据某个列表查询数据等函数，增加该注解用于简化对参数是否为空的检查，在需要检查参数的方法上添加该注解即可
 * 注意： 该注解只对spring托管的类有效，在Helper等纯静态类中无效，多用于Dao层，参考Spring切面实现
 * 支持检查的参数类型为：List, Set, Collection, Map，其他类型的参数不生效
 * 支持的返回值类型为：List, Set, Collection, Map, Optional，如果是其他类型的返回值，则该注解不生效
 * @see com.uusama.framework.tool.aspect.SkipEmptyParamAspect
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SkipEmpty {

    /**
     * 忽略检查的参数名称，不对配置的参数做检查，即允许配置的参数为空
     *
     * @return 参数名称列表
     */
    String[] ignoreParams() default {};
}
