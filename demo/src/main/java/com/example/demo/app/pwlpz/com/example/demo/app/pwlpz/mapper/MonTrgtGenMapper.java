package com.example.demo.app.pwlpz.mapper;

import com.example.demo.app.pwlpz.entity.MonTrgtGen;
import org.springframework.stereotype.Component;

/**
 * MonTrgtGen Mapper
 * <p>
 * 继承 BaseMapper，自动获得基础的 Entity <-> DTO 转换能力。
 * 如有特殊转换逻辑，请在此类中重写 dtoToEntity 或 entityToDto 方法。
 * </p>
 */
@Component
public class MonTrgtGenMapper extends com.example.demo.common.lee.mapper.BaseMapper {
    
    public MonTrgtGenMapper() {
        super(MonTrgtGen.class);
    }

    /*
    @Override
    public MonTrgtGen dtoToEntity(MonTrgtGenDto dto) {
        // 1. 调用父类默认转换
        MonTrgtGen entity = super.dtoToEntity(dto);
        // 2. 处理特殊逻辑
        if (entity != null) {
            // entity.setSomeField(convert(dto.getSomeField()));
        }
        return entity;
    }

    @Override
    public MonTrgtGenDto entityToDto(MonTrgtGen entity) {
        // 1. 调用父类默认转换
        MonTrgtGenDto dto = super.entityToDto(entity);
        // 2. 处理特殊逻辑
        if (dto != null) {
            // dto.setSomeField(convert(entity.getSomeField()));
        }
        return dto;
    }
    */
}
