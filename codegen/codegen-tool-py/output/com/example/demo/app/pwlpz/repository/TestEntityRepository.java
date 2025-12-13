package com.example.demo.app.pwlpz.repository;

import com.example.demo.app.pwlpz.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestEntityRepository extends JpaRepository<TestEntity, String> {
}
