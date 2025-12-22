package com.example.demo.common.lee.component;

import com.example.demo.common.lee.ResponseData;
import com.example.demo.common.lee.ResponseMsg;
import com.example.demo.common.lee.query.QueryDefaults;
import com.example.demo.common.lee.query.QueryField;
import com.example.demo.common.lee.query.QueryOperator;
import com.example.demo.common.lee.query.QueryRelation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Column;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 查询特性 Trait
 * <p>
 * 提供分页查询、列表查询、详情查询等读操作逻辑。
 * 包含复杂的动态查询构建逻辑。
 * </p>
 */
public interface QueryTrait<E, D, ID> extends BaseTrait<E, D, ID> {

    // --- 公开 API ---

    default ResponseData<Page<D>> findDtoByPage(Map<String, Object> params) {
        try {
            Page<E> result = findEntityByPage(params);
            Page<D> dtoList = result.map(e -> getMapper().entityToDto(e, params));
            return ResponseData.getSuccess(ResponseMsg.QUERY_SUCCESS, dtoList);
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.QUERY_FAIL, e);
        }
    }

    default ResponseData<List<D>> findDtoByList(Map<String, Object> params) {
        try {
            List<E> result = findAllEntity(params);
            List<D> dtoList = (result == null) ? new ArrayList<>() :
                    result.stream()
                            .map(e -> getMapper().entityToDto(e, params))
                            .collect(Collectors.toList());
            return ResponseData.getSuccess(ResponseMsg.QUERY_SUCCESS, dtoList);
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.QUERY_FAIL, e);
        }
    }

    default ResponseData<D> findDtoById(ID id) {
        try {
            return getRepository().findById(id)
                    .map(e -> ResponseData.getSuccess(ResponseMsg.QUERY_SUCCESS, getMapper().entityToDto(e)))
                    .orElseGet(() -> ResponseData.getError(ResponseMsg.QUERY_FAIL));
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.QUERY_FAIL, e);
        }
    }

    // --- 内部查询实现 ---

    default List<E> findAllEntity(Map<String, Object> params) {
        Specification<E> spec = getSpecification(params);
        return getRepository().findAll(spec);
    }

    default Page<E> findEntityByPage(Map<String, Object> params) {
        Specification<E> spec = getSpecification(params);
        Pageable pageable = createPageable(params, null);
        return getRepository().findAll(spec, pageable);
    }

    default Specification<E> getSpecification(Map<String, Object> params) {
        return (root, query, cb) -> {
            // 1. 处理软删除
            if (checkIfFieldExists(getEntityClass(), getIsDeleteFieldName())) {
                // 如果参数里没有指定删除状态，则默认只查未删除的
                if (!params.containsKey(getIsDeleteFieldName())) {
                    params.put(getIsDeleteFieldName(), getNotDeletedValue());
                }
            }

            List<Predicate> predicates = new ArrayList<>();
            if (params != null) {
                params.forEach((key, value) ->
                        addPredicate(predicates, query, root, cb, key, value, params)
                );
            }
            
            // 2. 动态 Group By
            addGroupBy(query, root, params);
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    default Pageable createPageable(Map<String, Object> params, Sort sort) {
        int page = Optional.ofNullable(params.get("page"))
                .map(Object::toString).map(Integer::parseInt).orElse(0) - 1;
        int rows = Optional.ofNullable(params.get("rows"))
                .map(Object::toString).map(Integer::parseInt).orElse(10);
        page = Math.max(page, 0);
        return (sort != null) ? PageRequest.of(page, rows, sort) : PageRequest.of(page, rows);
    }

    // --- 核心动态查询逻辑 (迁移自 BasicServiceImpl) ---
    /**
     * 根据传入的查询键和值，动态生成 JPA 谓词并加入到谓词列表。
     *
     * 支持以下特性：
     * - 内置键：`id`（IN/等值）、`creatorId`、软删除标识（当实体支持软删除时）
     * - 注解驱动：若实体字段标注了 {@link QueryField}，
     *   将按其 {@code operator/ignoreCase/alias} 配置生成条件；未标注则默认 EQ 精确匹配
     * - 键匹配：既支持实体字段名，也支持 `@Column(name)` 指定的列名（大小写不敏感）
     * - 支持的操作符：EQ, LIKE, IN, GT, GE, LT, LE, BETWEEN, STARTS_WITH, ENDS_WITH
     *
     * @param predicates      谓词列表
     * @param query           CriteriaQuery，用于创建子查询
     * @param root            实体的根引用
     * @param cb              条件构造器
     * @param key             查询键（字段名或列名）
     * @param value           查询值（不同操作符对值的类型要求不同）
     * @param params          其他参数
     */
    default void addPredicate(List<Predicate> predicates, CriteriaQuery<?> query, Root<E> root, CriteriaBuilder cb, String key, Object value, Map<String, Object> params) {
        if (value == null) {
            return;
        }
        String baseKey = key;
        QueryOperator overrideOp = null;
        boolean hintIgnoreCase = false;
        boolean strict = false;
        
        // 处理参数中的 strict 标志
        if (params != null && params.containsKey("strict")) {
            Object s = params.get("strict");
            if (s instanceof Boolean) {
                strict = (Boolean) s;
            } else if (s != null) {
                strict = Boolean.parseBoolean(s.toString());
            }
        }
        
        // 解析 key 中的修饰符 (e.g., name#LIKE#IC)
        if (key != null) {
            String[] parts = key.split("#");
            if (parts.length >= 1) {
                baseKey = parts[0];
            }
            if (parts.length >= 2) {
                try {
                    overrideOp = QueryOperator.valueOf(parts[1].toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (parts.length >= 3 && ("IC".equalsIgnoreCase(parts[2]) || "IGNORECASE".equalsIgnoreCase(parts[2]))) {
                hintIgnoreCase = true;
            }
        }

        // 处理特殊字段
        switch (baseKey) {
            case "id":
                if (value instanceof List) {
                    CriteriaBuilder.In<Object> inClause = cb.in(root.get("id"));
                    for (Object id : (List<?>) value) {
                        inClause.value(id);
                    }
                    predicates.add(inClause);
                } else {
                    predicates.add(cb.equal(root.get("id"), value));
                }
                return;
            case "creatorId":
                predicates.add(cb.equal(root.get("creatorId"), value));
                return;
            default:
                if (baseKey.equals(getIsDeleteFieldName())) {
                    predicates.add(cb.equal(root.get(getIsDeleteFieldName()), value));
                    return;
                }
        }

        Class<E> entityClass = getEntityClass();

        // 处理关联查询 (Namespace.field)
        if (baseKey.contains(".")) {
            String[] ns = baseKey.split("\\.");
            if (ns.length >= 2) {
                String relAlias = ns[0];
                String remoteField = ns[1];

                QueryRelation matched = null;
                QueryRelation[] relations = entityClass.getAnnotationsByType(QueryRelation.class);
                if (relations != null) {
                    for (QueryRelation r : relations) {
                        String alias = (r.name() != null && !r.name().isEmpty()) ? r.name() : r.remote().getSimpleName();
                        if (alias.equalsIgnoreCase(relAlias)) {
                            matched = r;
                            break;
                        }
                    }
                }

                if (matched == null) {
                    if (strict) {
                        throw new IllegalArgumentException("未找到关联关系: " + relAlias + " in entity " + entityClass.getSimpleName());
                    } else {
                        return;
                    }
                }

                Subquery<Object> sub = query.subquery(Object.class);
                Root<?> rr = sub.from(matched.remote());
                Predicate bridge = cb.equal(rr.get(matched.remoteKey()), root.get(matched.localKey()));

                QueryDefaults qd = entityClass.getAnnotation(QueryDefaults.class);
                Predicate remoteCond = buildOperatorPredicate(cb, rr.get(remoteField), (overrideOp != null ? overrideOp : (qd != null ? qd.operator() : QueryOperator.EQ)), hintIgnoreCase || (qd != null && qd.ignoreCase()), value, qd);

                if (remoteCond != null) {
                    sub.select(rr.get(matched.remoteKey())).where(cb.and(bridge, remoteCond));
                } else {
                    sub.select(rr.get(matched.remoteKey())).where(bridge);
                }
                predicates.add(cb.exists(sub));
                return;
            }
        }

        // 处理普通字段查询
        try {
            Field field;
            String fieldName = baseKey;
            try {
                field = findField(entityClass, baseKey);
            } catch (NoSuchFieldException nf) {
                field = null;
                // 尝试查找 @Column(name=...)
                for (Field f : entityClass.getDeclaredFields()) {
                    Column col = f.getAnnotation(Column.class);
                    if (col != null && col.name() != null && col.name().equalsIgnoreCase(baseKey)) {
                        field = f;
                        fieldName = f.getName();
                        break;
                    }
                    if (f.getName().equalsIgnoreCase(baseKey)) {
                        field = f;
                        fieldName = f.getName();
                        break;
                    }
                }
                if (field == null) {
                    if (strict) {
                        throw new IllegalArgumentException("查询字段不存在: " + key);
                    } else {
                        return;
                    }
                }
            }

            QueryField qf = field.getAnnotation(QueryField.class);
            QueryDefaults qd = entityClass.getAnnotation(QueryDefaults.class);
            if (qf != null && qf.alias() != null && !qf.alias().isEmpty()) {
                fieldName = qf.alias();
            }
            QueryOperator op = (overrideOp != null)
                    ? overrideOp
                    : (qf != null ? qf.operator() : (qd != null ? qd.operator() : QueryOperator.EQ));
            boolean ignoreCase = (qf != null && qf.ignoreCase()) || hintIgnoreCase || (qd != null && qd.ignoreCase());
            
            Predicate p = buildOperatorPredicate(cb, root.get(fieldName), op, ignoreCase, value, qd);
            if (p != null) {
                predicates.add(p);
            }
        } catch (SecurityException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    default Predicate buildOperatorPredicate(CriteriaBuilder cb, Path<?> path, QueryOperator op, boolean ignoreCaseHint, Object value, QueryDefaults qd) {
        QueryOperator useOp = (op != null) ? op : (qd != null ? qd.operator() : QueryOperator.EQ);
        switch (useOp) {
            case EQ:
                return cb.equal(path, value);
            case LIKE:
                if (value instanceof String) {
                    String v = (String) value;
                    if (ignoreCaseHint) {
                        return cb.like(cb.lower(path.as(String.class)), "%" + v.toLowerCase() + "%");
                    } else {
                        return cb.like(path.as(String.class), "%" + v + "%");
                    }
                }
                return null;
            case IN:
                if (value instanceof Collection) {
                    return path.in((Collection<?>) value);
                } else if (value instanceof Object[]) {
                    return path.in(java.util.Arrays.asList((Object[]) value));
                }
                return null;
            case GT:
                return cb.greaterThan(path.<Comparable>as(Comparable.class), (Comparable) value);
            case GE:
                return cb.greaterThanOrEqualTo(path.<Comparable>as(Comparable.class), (Comparable) value);
            case LT:
                return cb.lessThan(path.<Comparable>as(Comparable.class), (Comparable) value);
            case LE:
                return cb.lessThanOrEqualTo(path.<Comparable>as(Comparable.class), (Comparable) value);
            case STARTS_WITH:
                if (value instanceof String) {
                    String v = (String) value;
                    if (ignoreCaseHint) {
                        return cb.like(cb.lower(path.as(String.class)), v.toLowerCase() + "%");
                    } else {
                        return cb.like(path.as(String.class), v + "%");
                    }
                }
                return null;
            case ENDS_WITH:
                if (value instanceof String) {
                    String v = (String) value;
                    if (ignoreCaseHint) {
                        return cb.like(cb.lower(path.as(String.class)), "%" + v.toLowerCase());
                    } else {
                        return cb.like(path.as(String.class), "%" + v);
                    }
                }
                return null;
            case BETWEEN:
                if (value instanceof java.util.List && ((java.util.List<?>) value).size() == 2) {
                    Object start = ((java.util.List<?>) value).get(0);
                    Object end = ((java.util.List<?>) value).get(1);
                    if (start instanceof Comparable && end instanceof Comparable) {
                        return cb.between(path.<Comparable>as(Comparable.class), (Comparable) start, (Comparable) end);
                    }
                } else if (value instanceof Object[] && ((Object[]) value).length == 2) {
                    Object[] arr = (Object[]) value;
                    if (arr[0] instanceof Comparable && arr[1] instanceof Comparable) {
                        return cb.between(path.<Comparable>as(Comparable.class), (Comparable) arr[0], (Comparable) arr[1]);
                    }
                }
                return null;
            default:
                return cb.equal(path, value);
        }
    }

    default void addGroupBy(CriteriaQuery<?> query, Root<E> root, Map<String, Object> params) {
        if (params.containsKey("groupByFields")) {
            Object groupByFields = params.get("groupByFields");
            if (groupByFields instanceof String) {
                String fieldsStr = (String) groupByFields;
                List<Expression<?>> groupByExpressions = new ArrayList<>();
                for (String field : fieldsStr.split(",")) {
                    groupByExpressions.add(root.get(field.trim()));
                }
                query.groupBy(groupByExpressions);
            }
        } else if (params.containsKey("groupByField")) {
            Object groupByField = params.get("groupByField");
            if (groupByField instanceof String) {
                query.groupBy(root.get((String) groupByField));
            }
        }
    }
}
