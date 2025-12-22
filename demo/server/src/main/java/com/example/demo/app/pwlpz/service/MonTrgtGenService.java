package com.example.demo.app.pwlpz.service;

import com.example.demo.app.pwlpz.dto.MonTrgtGenDto;
import com.example.demo.app.pwlpz.entity.MonTrgtGenEntity;
import com.example.demo.app.pwlpz.mapper.MonTrgtGenMapper;
import com.example.demo.app.pwlpz.repository.MonTrgtGenRepo;
import com.example.demo.common.lee.component.AbstractTraitService;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

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

    @Override
    public void addPredicate(
            List<Predicate> predicates,
            CriteriaQuery<?> query,
            Root<MonTrgtGenEntity> root,
            CriteriaBuilder cb,
            String key,
            Object value,
            Map<String, Object> params
    ) {
        if (value == null) return;
        callDefaultAddPredicate(predicates, query, root, cb, key, value, params);

        // 自定义规则示例：status 支持多值 IN
        if ("status".equals(key)) {
            if (value instanceof Iterable) {
                CriteriaBuilder.In<Object> in = cb.in(root.get("status"));
                for (Object v : (Iterable<?>) value) in.value(v);
                predicates.add(in);
            } else {
                predicates.add(cb.equal(root.get("status"), value));
            }
        }
    }
}
