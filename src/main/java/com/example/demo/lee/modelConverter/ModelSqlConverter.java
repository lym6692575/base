package com.example.demo.lee.modelConverter;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.internal.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class ModelSqlConverter {
    /**
     * 用于创建专门处理单个SQL查询结果的转换器
     * @param conversionFunction 转换函数
     * @return Converter
     */
    public static <S, T> Converter<S, T> createSingleResultSqlConverter(Function<S, T> conversionFunction) {
        return new AbstractConverter<S, T>() {
            @Override
            protected T convert(S source) {
                try {
                    return conversionFunction.apply(source);
                } catch (Exception e) {
                    System.out.println("转换时发生异常: " + e.getMessage());
                    return null;
                }
            }
        };
    }

    /**
     * 创建一个用于格式化 LocalDateTime 的转换器
     * @param format 格式化模式字符串
     * @return Converter<LocalDateTime, String>
     */
    public static Converter<LocalDateTime, String> createLocalDateTimeFormatter(String format) {
        Assert.notNull(format, "格式化模式不能为null");
        return new AbstractConverter<LocalDateTime, String>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

            @Override
            protected String convert(LocalDateTime source) {
                if (source == null) {
                    return null;
                }
                try {
                    return formatter.format(source);
                } catch (Exception e) {
                    System.out.println("日期时间格式化时发生异常: " + e.getMessage());
                    return null;
                }
            }
        };
    }
    /**
     * 创建一个基于JPA的转换器，根据提供的实体类和查询条件返回整个实体
     * @param entityClass 实体类
     * @param entityManager EntityManager实例
     * @return Converter
     */
    public static <ID, T> Converter<ID, T> createEntityConverter(Class<T> entityClass, EntityManager entityManager) {
        return new AbstractConverter<ID, T>() {
            @Override
            protected T convert(ID source) {
                if (source == null) {
                    return null;
                }

                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaQuery<T> query = cb.createQuery(entityClass);
                Root<T> root = query.from(entityClass);

                query.select(root)  // 返回整个实体
                        .where(cb.equal(root.get("id"), source));
                try {
                    return entityManager.createQuery(query).getSingleResult();
                } catch (Exception e) {
                    System.out.println("查询数据库时发生异常: " + e.getMessage());
                    return null;
                }
            }
        };
    }
}
