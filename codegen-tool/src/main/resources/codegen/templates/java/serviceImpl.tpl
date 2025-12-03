package {{package}};

import com.dqjq.base.common.lee.service.basic.basicService.impl.BasicPlusServiceImpl;
import {{dtoPackage}}.{{dtoName}};
import {{entityPackage}}.{{entityName}}Entity;
import {{mapperPackage}}.{{entityName}}Mapper;
import {{repoPackage}}.{{entityName}}Repo;
import {{servicePackage}}.{{entityName}}Service;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

@Service
public class {{entityName}}ServiceImpl extends BasicPlusServiceImpl<{{entityName}}Entity, {{dtoName}}, {{idType}}> implements {{entityName}}Service {
    private final {{entityName}}Repo repo;

    public {{entityName}}ServiceImpl({{entityName}}Repo repo, {{entityName}}Mapper mapper) {
        super(repo, mapper, {{entityName}}Entity.class);
        this.repo = repo;
    }

    @Override
    public void addPredicate(List<Predicate> predicates, CriteriaQuery<?> criteriaQuery, Root<{{entityName}}Entity> root, CriteriaBuilder criteriaBuilder, String key, Object value, Map<String, Object> params) {
{{predicates}}    }
}