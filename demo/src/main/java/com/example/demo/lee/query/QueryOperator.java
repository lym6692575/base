package com.example.demo.lee.query;

/**
 * 通用查询操作符枚举。
 *
 * EQ           等值匹配（默认）
 * LIKE         模糊匹配（可配合 ignoreCase）
 * IN           集合包含匹配（支持 Collection / 数组）
 * GT/GE/LT/LE  比较操作（要求值实现 Comparable）
 * BETWEEN      区间匹配（传 [start, end] 或长度为2的数组）
 * STARTS_WITH  前缀匹配
 * ENDS_WITH    后缀匹配
 */
public enum QueryOperator {
    EQ,
    LIKE,
    IN,
    GT,
    LT,
    GE,
    LE,
    BETWEEN,
    STARTS_WITH,
    ENDS_WITH
}
