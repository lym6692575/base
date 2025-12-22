package com.example.demo.common.lee.mapper;

import org.springframework.beans.BeanUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基础 Mapper 抽象类
 * <p>
 * 提供基于反射的默认对象转换实现 (Entity <-> DTO)。
 * 使用 Spring BeanUtils 进行属性拷贝。
 * </p>
 *
 * @param <D> DTO 类型
 * @param <E> Entity 类型
 */
public abstract class BaseMapper<D, E> {

    private final Class<D> dtoClass;
    private final Class<E> entityClass;

    protected BaseMapper(Class<D> dtoClass, Class<E> entityClass) {
        this.dtoClass = dtoClass;
        this.entityClass = entityClass;
    }

    /**
     * 将 DTO 对象转换为实体对象。
     * @param dto 源 DTO 对象
     * @return 转换后的实体对象
     */
    public E dtoToEntity(D dto) {
        if (dto == null) {
            return null;
        }
        try {
            E entity = entityClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(dto, entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Entity转换失败 [" + entityClass.getName() + "]: " + e.getMessage(), e);
        }
    }

    /**
     * 将实体对象转换为 DTO 对象。
     * @param entity 源实体对象
     * @return 转换后的 DTO 对象
     */
    public D entityToDto(E entity) {
        if (entity == null) {
            return null;
        }
        try {
            D dto = dtoClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("DTO转换失败 [" + dtoClass.getName() + "]: " + e.getMessage(), e);
        }
    }

    /**
     * 兼容旧接口：带 context 的转换 (目前忽略 context)
     */
    public D entityToDto(E entity, Map<String, Object> context) {
        return entityToDto(entity);
    }

    /**
     * 批量转换: List<Entity> -> List<DTO>
     */
    public List<D> toDtoList(List<E> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }
        return entityList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    /**
     * 批量转换: List<DTO> -> List<Entity>
     */
    public List<E> toEntityList(List<D> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoList.stream()
                .map(this::dtoToEntity)
                .collect(Collectors.toList());
    }
}
