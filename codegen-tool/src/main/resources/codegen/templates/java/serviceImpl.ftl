package ${package};

import ${serviceBaseClass};
import ${dtoPackage}.${dtoName};
import ${entityPackage}.${entityName}Entity;
import ${mapperPackage}.${entityName}Mapper;
import ${repoPackage}.${entityName}Repo;
import ${servicePackage}.${entityName}Service;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

@Service
public class ${entityName}ServiceImpl extends BasicPlusServiceImpl<${entityName}Entity, ${dtoName}, ${idType}> implements ${entityName}Service {
    private final ${entityName}Repo repo;

    public ${entityName}ServiceImpl(${entityName}Repo repo, ${entityName}Mapper mapper) {
        super(repo, mapper, ${entityName}Entity.class);
        this.repo = repo;
    }

    @Override
    public void addPredicate(List<Predicate> predicates, CriteriaQuery<?> criteriaQuery, Root<${entityName}Entity> root, CriteriaBuilder criteriaBuilder, String key, Object value, Map<String, Object> params) {
        super.addPredicate(predicates, criteriaQuery, root, criteriaBuilder, key, value, params);
<#list fields as field>
    <#if field.type == "String">
        if ("${field.name}".equals(key) && value instanceof String) {
            predicates.add(criteriaBuilder.like(root.get("${field.name}"), "%" + value + "%"));
        }
    <#else>
        if ("${field.name}".equals(key) && value != null) {
            predicates.add(criteriaBuilder.equal(root.get("${field.name}"), value));
        }
    </#if>
</#list>
    }
}
