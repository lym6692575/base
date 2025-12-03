package com.example.demo.lee.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 基础仓库接口，继承 JpaRepository 和 JpaSpecificationExecutor。
 * @param <Entity> 实体类的类型
 * @param <ID> 实体类ID的类型
 */
@NoRepositoryBean
public interface BasicRepository<Entity, ID> extends JpaRepository<Entity, ID>, JpaSpecificationExecutor<Entity> {
}