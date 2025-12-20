package com.example.demo.common.lee.component;

import com.example.demo.common.lee.mapper.BaseMapper;
import com.example.demo.common.lee.repository.BasicRepository;
import lombok.Getter;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

/**
 * 抽象 Trait Service 基类
 * <p>
 * 聚合了 Query, Save, Delete 能力。
 * </p>
 */
@Transactional
public abstract class AbstractTraitService<E, D, ID>
        implements
        QueryTrait<E, D, ID>,
        SaveTrait<E, D, ID>,
        DeleteTrait<E, D, ID> {

    @Getter
    protected final BasicRepository<E, ID> repository;
    @Getter
    protected final BaseMapper<D, E> mapper;

    @PersistenceContext
    @Getter
    protected EntityManager entityManager;

    private final Class<E> entityClass;

    public AbstractTraitService(BasicRepository<E, ID> repository,
                                BaseMapper<D, E> mapper,
                                Class<E> entityClass) {
        this.repository = repository;
        this.mapper = mapper;
        this.entityClass = entityClass;
    }

    @Override
    public Class<E> getEntityClass() {
        return entityClass;
    }

    /**
     * 调用 QueryTrait 的默认 addPredicate 实现
     */
    protected void callDefaultAddPredicate(List<Predicate> predicates,
                                           CriteriaQuery<?> query,
                                           Root<E> root,
                                           CriteriaBuilder cb,
                                           String key,
                                           Object value,
                                           Map<String, Object> params) {
        QueryTrait.super.addPredicate(predicates, query, root, cb, key, value, params);
    }
}
