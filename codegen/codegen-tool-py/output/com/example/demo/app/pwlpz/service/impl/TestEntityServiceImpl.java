package com.example.demo.app.pwlpz.service.impl;

import com.example.demo.app.pwlpz.entity.TestEntity;
import com.example.demo.app.pwlpz.dto.TestEntityDto;
import com.example.demo.app.pwlpz.service.TestEntityService;
import com.example.demo.app.pwlpz.repository.TestEntityRepository;
import com.example.demo.app.pwlpz.mapper.TestEntityMapper;
import org.springframework.stereotype.Service;

@Service
public class TestEntityServiceImpl implements TestEntityService {
    private final TestEntityRepository repository;

    public TestEntityServiceImpl(TestEntityRepository repository) {
        this.repository = repository;
    }
    
    // 可以在此实现服务方法
}
