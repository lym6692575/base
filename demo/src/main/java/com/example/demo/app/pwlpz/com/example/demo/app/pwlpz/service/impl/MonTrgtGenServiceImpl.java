package com.example.demo.app.pwlpz.service.impl;

import com.example.demo.app.pwlpz.entity.MonTrgtGen;
import com.example.demo.app.pwlpz.dto.MonTrgtGenDto;
import com.example.demo.app.pwlpz.service.MonTrgtGenService;
import com.example.demo.app.pwlpz.repository.MonTrgtGenRepository;
import com.example.demo.app.pwlpz.mapper.MonTrgtGenMapper;
import org.springframework.stereotype.Service;

@Service
public class MonTrgtGenServiceImpl extends com.example.demo.common.lee.Basic.impl.BasicPlusServiceImpl<MonTrgtGen, MonTrgtGenDto, String> implements MonTrgtGenService {
    private final MonTrgtGenRepository repository;

    public MonTrgtGenServiceImpl(MonTrgtGenRepository repository, MonTrgtGenMapper mapper) {
        super(repository, mapper, MonTrgtGen.class);
        this.repository = repository;
    }
}
