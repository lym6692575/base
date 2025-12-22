package com.example.demo.app.pwlpz.mapper;

import com.example.demo.app.pwlpz.dto.MonTrgtGenDto;
import com.example.demo.app.pwlpz.entity.MonTrgtGenEntity;
import com.example.demo.common.lee.mapper.BaseMapper;
import org.springframework.stereotype.Component;

@Component
public class MonTrgtGenMapper extends BaseMapper<MonTrgtGenDto, MonTrgtGenEntity> {

    public MonTrgtGenMapper() {
        super(MonTrgtGenDto.class, MonTrgtGenEntity.class);
    }

    @Override
    public MonTrgtGenDto entityToDto(MonTrgtGenEntity entity) {
        MonTrgtGenDto dto = super.entityToDto(entity);
        if (dto != null) {
            dto.setTrgtNameTest(entity.getTrgtName().equals("1") ? "is1" : "isNot1");
        }

        return dto;
    }
}
