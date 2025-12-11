package com.example.demo.app.pwlpz.service;

import com.example.demo.app.pwlpz.dto.MonTrgtGenDto;
import com.example.demo.app.pwlpz.entity.MonTrgtGenEntity;
import com.example.demo.app.pwlpz.mapper.MonTrgtGenMapper;
import com.example.demo.app.pwlpz.repository.MonTrgtGenRepo;
import com.example.demo.common.lee.component.AbstractTraitService;
import org.springframework.stereotype.Service;

/**
 * 业务服务类
 * <p>
 * 采用单层 Service 模式，直接继承 AbstractTraitService 获得所有能力。
 * 无需定义额外的接口。
 * </p>
 */
@Service
public class MonTrgtGenService extends AbstractTraitService<MonTrgtGenEntity, MonTrgtGenDto, String> {

    public MonTrgtGenService(MonTrgtGenRepo repo, MonTrgtGenMapper mapper) {
        super(repo, mapper, MonTrgtGenEntity.class);
    }
}
