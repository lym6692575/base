package com.example.demo.lee.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标注需要记录修改日志的字段。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LogField {
    /**
     * 日志中显示的字段名称。
     */
    String displayName();
}