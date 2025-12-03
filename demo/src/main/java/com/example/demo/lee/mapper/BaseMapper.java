package com.example.demo.lee.mapper;

import java.util.Map;

public interface BaseMapper<DTO, Entity> {

    /**
     * 将 DTO 对象转换为实体对象。
     * @param dto 源 DTO 对象
     * @param entityType 目标实体类的 Class 对象
     * @return 转换后的实体对象
     */
    Entity dtoToEntity(DTO dto, Class<Entity> entityType);

    /**
     * 将实体对象转换为 DTO 对象。
     * @param entity 源实体对象
     * @param dtoType 目标 DTO 的 Class 对象
     * @return 转换后的 DTO 对象
     */
    DTO entityToDto(Entity entity, Class<DTO> dtoType);
    default DTO entityToDto(Entity entity, Class<DTO> dtoType, Map<String,Object> context) {
        // 默认行为：不使用 context，直接调用无参版
        return entityToDto(entity, dtoType);
    }
}
