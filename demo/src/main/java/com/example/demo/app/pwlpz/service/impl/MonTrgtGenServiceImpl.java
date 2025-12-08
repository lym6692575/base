package com.example.demo.app.pwlpz.service.impl;

import com.dqjq.base.common.lee.service.basic.basicService.impl.BasicPlusServiceImpl;
import com.example.demo.app.pwlpz.dto.MonTrgtGenDto;
import com.example.demo.app.pwlpz.entity.MonTrgtGenEntity;
import com.example.demo.app.pwlpz.mapper.MonTrgtGenMapper;
import com.example.demo.app.pwlpz.repository.MonTrgtGenRepo;
import com.example.demo.app.pwlpz.service.MonTrgtGenService;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

@Service
public class MonTrgtGenServiceImpl extends BasicPlusServiceImpl<MonTrgtGenEntity, MonTrgtGenDto, String> implements MonTrgtGenService {
    private final MonTrgtGenRepo repo;

    public MonTrgtGenServiceImpl(MonTrgtGenRepo repo, MonTrgtGenMapper mapper) {
        super(repo, mapper, MonTrgtGenEntity.class);
        this.repo = repo;
    }

    @Override
    public void addPredicate(List<Predicate> predicates, CriteriaQuery<?> criteriaQuery, Root<MonTrgtGenEntity> root, CriteriaBuilder criteriaBuilder, String key, Object value, Map<String, Object> params) {
        super.addPredicate(predicates, criteriaQuery, root, criteriaBuilder, key, value, params);
        if ("trgtName".equals(key) && value instanceof String) {
            predicates.add(criteriaBuilder.like(root.get("trgtName"), "%" + value + "%"));
        }
        if ("plantName".equals(key) && value instanceof String) {
            predicates.add(criteriaBuilder.like(root.get("plantName"), "%" + value + "%"));
        }
        if ("activeChk".equals(key) && value instanceof String) {
            predicates.add(criteriaBuilder.like(root.get("activeChk"), "%" + value + "%"));
        }
    }
}
