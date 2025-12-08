package com.example.demo.common.lee.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 作用于实体类的查询默认配置注解。
 *
 * 标注在实体类上后，该实体未显式标注 {@link QueryField} 的字段
 * 将采用此处定义的默认查询行为（可被参数键后缀或字段级注解覆盖）。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QueryDefaults {
    /**
     * 默认查询操作符，字段未显式配置时生效。
     */
    QueryOperator operator() default QueryOperator.EQ;

    /**
     * 默认是否忽略大小写，仅在字符串匹配操作符（LIKE/STARTS_WITH/ENDS_WITH）生效。
     */
    boolean ignoreCase() default false;
}
