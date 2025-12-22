package com.example.demo.app.test.service;

import com.example.demo.app.test.entity.testEntity;
import com.example.demo.app.test.dto.testDto;
import com.example.demo.app.test.mapper.testMapper;
import com.example.demo.app.test.repository.testRepository;
import com.example.demo.common.lee.component.AbstractTraitService;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Map;

/**
 * 业务服务类
 * <p>
 * 采用单层 Service 模式，直接继承 AbstractTraitService 获得所有能力。
 * </p>
 */
@Service
public class testService extends AbstractTraitService<testEntity, testDto, String> {

    public testService(testRepository repo, testMapper mapper) {
        super(repo, mapper, testEntity.class);
    }

    @Override
    public void addPredicate(
            List<Predicate> predicates,
            CriteriaQuery<?> query,
            Root<testEntity> root,
            CriteriaBuilder cb,
            String key,
            Object value,
            Map<String, Object> params
    ) {
        if (value == null) return;
        
        // 调用父类默认查询逻辑
        callDefaultAddPredicate(predicates, query, root, cb, key, value, params);

        /*
         * 自定义查询规则逻辑示例
         * 你可以根据业务需要在这里扩展，例如处理 status 的 IN 查询
         */
        // if ("status".equals(key)) {
        //     if (value instanceof Iterable) {
        //         CriteriaBuilder.In<Object> in = cb.in(root.get("status"));
        //         for (Object v : (Iterable<?>) value) in.value(v);
        //         predicates.add(in);
        //     } else {
        //         predicates.add(cb.equal(root.get("status"), value));
        //     }
        // }
    }
}