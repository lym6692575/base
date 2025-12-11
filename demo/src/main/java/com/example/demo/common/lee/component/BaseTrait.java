package com.example.demo.common.lee.component;

import com.example.demo.common.lee.mapper.BaseMapper;
import com.example.demo.common.lee.repository.BasicRepository;
import javax.persistence.EntityManager;

/**
 * 基础特性 Trait
 * <p>
 * 提供所有 Trait 组件所需的基础设施访问能力。
 * 任何实现此接口的 Service 必须提供 Repository、Mapper 和 EntityManager 的访问权限。
 * </p>
 */
public interface BaseTrait<E, D, ID> {

    /**
     * 获取 Repository
     */
    BasicRepository<E, ID> getRepository();

    /**
     * 获取 Mapper
     */
    BaseMapper<D, E> getMapper();

    /**
     * 获取 EntityManager (用于复杂查询构建)
     */
    EntityManager getEntityManager();
    
    /**
     * 获取 Entity 类型
     */
    Class<E> getEntityClass();
}
