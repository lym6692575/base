package com.example.demo.lee.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明当前实体与远端实体的逻辑关联关系（不依赖数据库外键）。
 *
 * 用于在通用查询层根据键如 "RemoteSimpleName.field#OP" 自动构建 EXISTS 子查询：
 * EXISTS (SELECT 1 FROM Remote r WHERE r.remoteKey = root.localKey AND [r.field 条件])
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(QueryRelations.class)
public @interface QueryRelation {
    /**
     * 远端实体类（JPA Entity）。
     */
    Class<?> remote();

    /**
     * 本实体中的桥接键（通常是保存远端实体主键的字段名）。
     */
    String localKey();

    /**
     * 远端实体中的桥接键（通常是主键，默认为 "id"）。
     */
    String remoteKey() default "id";

    /**
     * 关联别名，用于参数键前缀匹配；为空时使用 remote 实体的简单类名。
     */
    String name() default "";
}
