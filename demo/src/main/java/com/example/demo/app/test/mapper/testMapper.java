package com.example.demo.app.test.mapper;

import com.example.demo.app.test.entity.testEntity;
import com.example.demo.app.test.dto.testDto;
import org.springframework.stereotype.Component;

import com.example.demo.common.lee.mapper.BaseMapper;

/**
 * testMapper
* <p>
* 继承 BaseMapper，自动获得基础的 Entity <-> DTO 转换能力。
* </p> */
@Component
public class testMapper extends BaseMapper<testDto, testEntity> {
    
    public testMapper() {
        super(testDto.class, testEntity.class);
    }    
    /*
    @Override
    public testEntity dtoToEntity(testDto dto) {
        // 1. 调用父类默认转换
        testEntity entity = super.dtoToEntity(dto);
        // 2. 处理特殊逻辑
        return entity;
    }
    @Override
    public testDto entityToDto(testEntity entity) {
        // 1. 调用父类默认转换
        testDto dto = super.entityToDto(entity);
        // 2. 处理特殊逻辑
        return dto;
    }
    */
}