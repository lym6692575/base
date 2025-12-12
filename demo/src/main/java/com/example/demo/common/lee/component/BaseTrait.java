package com.example.demo.common.lee.component;

import com.example.demo.common.lee.mapper.BaseMapper;
import com.example.demo.common.lee.repository.BasicRepository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;

/**
 * 基础特性 Trait
 * <p>
 * 提供所有 Trait 组件所需的基础设施访问能力。
 * 任何实现此接口的 Service 必须提供 Repository、Mapper 和 EntityManager 的访问权限。
 * </p>
 */
public interface BaseTrait<E, D, ID> {

    /**
     * 获取 Repository
     */
    BasicRepository<E, ID> getRepository();

    /**
     * 获取 Mapper
     */
    BaseMapper<D, E> getMapper();

    /**
     * 获取 EntityManager (用于复杂查询构建)
     */
    EntityManager getEntityManager();
    
    /**
     * 获取 Entity 类型
     */
    Class<E> getEntityClass();

    // --- 全局配置 (可重写) ---

    default String getIsDeleteFieldName() {
        return "isDelete";
    }

    default Object getDeletedValue() {
        return 1;
    }

    default Object getNotDeletedValue() {
        return 0;
    }

    // --- 共用反射工具方法 ---

    default Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    default void setFieldIfExists(Object entity, String fieldName, Object value) {
        try {
            Field field = findField(entity.getClass(), fieldName);
            field.set(entity, value);
        } catch (Exception e) {
            // ignore
        }
    }

    default boolean checkIfFieldExists(Class<?> clazz, String fieldName) {
        if (!StringUtils.hasText(fieldName)) {
            return false;
        }
        try {
            findField(clazz, fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}
