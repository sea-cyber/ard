package com.project.ard.dataretrieval.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 工作流类型注解
 * 用于标记工作流处理器支持的类型
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WorkflowType {
    
    /**
     * 支持的工作流类型标识
     * @return 工作流类型
     */
    String value();
    
    /**
     * 工作流描述
     * @return 描述信息
     */
    String description() default "";
}

