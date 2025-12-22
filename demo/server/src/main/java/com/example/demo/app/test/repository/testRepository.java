package com.example.demo.app.test.repository;

import com.example.demo.app.test.entity.testEntity;
import com.example.demo.common.lee.repository.BasicRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface testRepository extends BasicRepository<testEntity, String> {
}
