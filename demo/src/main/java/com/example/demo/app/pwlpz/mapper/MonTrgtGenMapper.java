package com.example.demo.app.pwlpz.mapper;

import com.dqjq.base.common.lee.mapper.BaseMapper;
import com.example.demo.app.pwlpz.dto.MonTrgtGenDto;
import com.example.demo.app.pwlpz.entity.MonTrgtGenEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MonTrgtGenMapper implements BaseMapper<MonTrgtGenDto, MonTrgtGenEntity> {
    private final ModelMapper modelMapper;

    public MonTrgtGenMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public MonTrgtGenEntity dtoToEntity(MonTrgtGenDto dto, Class<MonTrgtGenEntity> entityType) {
        return modelMapper.map(dto, entityType);
    }

    @Override
    public MonTrgtGenDto entityToDto(MonTrgtGenEntity entity, Class<MonTrgtGenDto> dtoType) {
        return modelMapper.map(entity, dtoType);
    }
}
