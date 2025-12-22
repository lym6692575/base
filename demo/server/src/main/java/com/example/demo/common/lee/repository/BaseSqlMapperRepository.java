package com.example.demo.common.lee.repository;

import com.example.demo.common.lee.entity.BaseSqlMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseSqlMapperRepository extends JpaRepository<BaseSqlMapper, String> {
    BaseSqlMapper findByMappingKey(String mappingKey);
}
