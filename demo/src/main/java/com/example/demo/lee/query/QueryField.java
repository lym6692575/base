package com.example.demo.lee.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为实体字段声明查询行为的注解。
 *
 * 使用该注解后，通用查询会根据字段的配置自动在 {@code addPredicate} 中拼接查询条件，
 * 默认行为为精确匹配（EQ）。
 *
 * 示例：
 * <pre>
 * @QueryField // 默认 EQ 精确匹配
 * private String units;
 *
 * @QueryField(operator = QueryOperator.LIKE, ignoreCase = true)
 * private String trgtName; // 忽略大小写的 LIKE 模糊匹配
 *
 * @QueryField(operator = QueryOperator.IN)
 * private String shftArea; // IN 集合匹配
 *
 * @QueryField(alias = "UNITS")
 * private String unitsAlias; // 使用别名映射到另一查询字段
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface QueryField {
    /**
     * 指定查询操作符，默认 EQ（等值）。
     */
    QueryOperator operator() default QueryOperator.EQ;

    /**
     * 查询字段别名。当实体属性名与查询字段路径不一致时使用。
     * 例如需要映射到不同列名或未来支持关联路径时，可设置此值。
     */
    String alias() default "";

    /**
     * 是否忽略大小写，仅在 LIKE/STARTS_WITH/ENDS_WITH 字符串匹配场景生效。
     */
    boolean ignoreCase() default false;
}
