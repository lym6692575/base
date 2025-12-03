package com.example.demo.lee.Basic.impl;


import com.example.demo.lee.query.QueryDefaults;
import com.example.demo.lee.query.QueryField;
import com.example.demo.lee.query.QueryOperator;
import com.example.demo.lee.query.QueryRelation;
import com.example.demo.lee.repository.BasicRepository;
import com.example.demo.lee.Basic.BasicService;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.persistence.Column;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * 基础服务实现类，提供基本的数据库操作实现。
 *
 * @param <Entity> 实体类类型
 * @param <ID>     实体类ID类型
 */
public abstract class BasicServiceImpl<Entity, ID> implements BasicService<Entity, ID> {
    private final BasicRepository<Entity, ID> repository;
    @Getter
    private final String isDeleteFieldName;
    @Getter
    private final Object notDeletedValue;
    @Getter
    private final Object deletedValue;
    private final boolean hasSoftDeleteField;
    private final Class<Entity> entityClass;
    @PersistenceContext
    private EntityManager em;

    /**
     * 构造函数，注入JPA仓库和未删除值。
     *
     * @param repository        用于数据库操作的JPA仓库
     * @param isDeleteFieldName 软删除标记字段名
     * @param notDeletedValue   表示“未删除”的值
     */
    public BasicServiceImpl(
            BasicRepository<Entity, ID> repository,
            String isDeleteFieldName,
            Object notDeletedValue,
            Object deletedValue,
            Class<Entity> entityClass
    ) {
        this.repository = repository;
        this.isDeleteFieldName = isDeleteFieldName;
        this.notDeletedValue = notDeletedValue;
        this.deletedValue = deletedValue;
        this.hasSoftDeleteField = checkIfFieldExists(entityClass, isDeleteFieldName);
        this.entityClass = entityClass;
    }

    /**
     * 构造函数，注入JPA仓库，使用默认软删除字段名和未删除值。
     *
     * @param repository 用于数据库操作的JPA仓库
     */
    public BasicServiceImpl(BasicRepository<Entity, ID> repository, Class<Entity> entityClass) {
        this(repository, "isDelete", 0, 1, entityClass); // 默认字段名为 "isDelete" 且未删除值为 0
    }

    /**
     * 查找所有实体，可以包含查询参数进行过滤。
     *
     * @param params 查询参数
     * @return 返回所有符合条件的实体列表
     */
    @Override
    public List<Entity> findAllEntity(Map<String, Object> params) {
        if (hasSoftDeleteField) {
            params.put(isDeleteFieldName, notDeletedValue);
        }
        Specification<Entity> spec = getSpecification(params);
        return repository.findAll(spec);
    }

    /**
     * 分页查询实体列表。
     *
     * @param params 查询参数
     * @return 返回一个包含分页结果的 Page(domain) 对象
     */
    @Override
    public Page<Entity> findEntityByPage(Map<String, Object> params) {
        if (hasSoftDeleteField) {
            params.put(isDeleteFieldName, notDeletedValue);
        }
        Specification<Entity> spec = getSpecification(params);
        Pageable pageable = createPageable(params, null);
        return repository.findAll(spec, pageable);
    }

    /**
     * 分页并排序查询实体列表。
     *
     * @param params 查询参数
     * @param sort   排序参数
     * @return 返回一个包含分页结果的 Page(domain) 对象
     */
    @Override
    public Page<Entity> findEntityByPageAndSort(Map<String, Object> params, Sort sort) {
        if (hasSoftDeleteField) {
            params.put(isDeleteFieldName, notDeletedValue);
        }
        Specification<Entity> spec = getSpecification(params);
        Pageable pageable = createPageable(params, sort);
        return repository.findAll(spec, pageable);
    }

    /**
     * 根据ID查找实体。
     *
     * @param id 实体的ID
     * @return 返回一个包含实体的Optional，如果未找到则为空
     */
    @Override
    public Optional<Entity> findById(ID id) {
        return repository.findById(id);
    }

    /**
     * 保存单个实体。
     *
     * @param entity 要保存的实体
     * @return
     */
    @Override
    public Entity saveEntity(Entity entity) {
        return repository.save(entity);
    }

    /**
     * 根据查询条件对指定字段求和（支持动态拼接查询条件）
     *
     * @param params      查询参数 map，key 对应 addPredicate 支持的字段
     * @param fieldName   要求和的实体字段名（例如 "weight"）
     * @param entityClass 实体类型 Class 对象，对应类上定义的 Entity 泛型
     * @return 符合条件的字段求和结果（null 会被转为 0）
     */
    @Override
    public BigDecimal sumByField(Map<String, Object> params,
                                 String fieldName,
                                 Class<Entity> entityClass) {
        // 1. 拿到 CriteriaBuilder，用来构造动态查询
        CriteriaBuilder cb = em.getCriteriaBuilder();
        // 2. 创建返回 BigDecimal 的查询对象
        CriteriaQuery<BigDecimal> cq = cb.createQuery(BigDecimal.class);
        // 3. 定义查询根，从实体类型出发
        Root<Entity> root = cq.from(entityClass);

        // 4. 动态拼接 Predicate 列表
        List<Predicate> predicates = new ArrayList<>();
        params.forEach((key, value) ->
                addPredicate(predicates, cq, root, cb, key, value, params)
        );

        // 5. 构造 sum 聚合，并用 coalesce 防止结果为 null
        Expression<BigDecimal> sumExpr   = cb.sum(root.get(fieldName));
        Expression<BigDecimal> roundExpr = cb.function(
                "ROUND",
                BigDecimal.class,
                sumExpr,
                cb.literal(3)       // 保留 3 位
        );
        cq.select(cb.coalesce(roundExpr, BigDecimal.ZERO))
                .where(predicates.toArray(new Predicate[0]));

        // 6. 执行查询，拿到最终结果
        return em.createQuery(cq).getSingleResult();
    }


    /**
     * 保存单个实体。
     *
     * @param entity 要保存的实体
     */
    @Override
    public Entity saveEntityWithReturn(Entity entity) {
        return repository.save(entity);
    }
    /**
     * 保存多个实体。
     *
     * @param entities 要保存的实体列表
     */
    @Override
    public void saveAllEntity(List<Entity> entities) {
        repository.saveAll(entities);
    }

    /**
     * 更新实体的特定字段。
     *
     * @param id        实体的ID
     * @param fieldName 要更新的字段名
     * @param newValue  新的值
     * @return 更新后的实体
     */
    @Override
    @Transactional
    public Entity updateField(ID id, String fieldName, Object newValue) {
        Entity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with ID: " + id));

        try {
            Field field = findField(entity.getClass(), fieldName);  // 获取实体类中的字段
            field.setAccessible(true);  // 设置私有字段可访问
            field.set(entity, newValue);  // 更新字段值
            repository.save(entity);  // 保存更新后的实体
            return entity;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("实体未找到: " + fieldName, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("访问字段失败: " + fieldName, e);
        }
    }

    /**
     * 批量更新实体的特定字段。
     *
     * @param ids       实体ID列表
     * @param fieldName 要更新的字段名
     * @param newValue  新的值
     * @return 更新的实体数量
     */
    @Override
    @Transactional
    public Integer updateFieldBatch(List<ID> ids, String fieldName, Object newValue) {
        List<Entity> entities = repository.findAllById(ids);
        if (entities.isEmpty()) {
            throw new EntityNotFoundException("实体未找到");
        }

        Field field;
        try {
            field = findField(entities.get(0).getClass(), fieldName);  // 使用新方法查找字段
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Field not found: " + fieldName, e);
        }

        try {
            for (Entity entity : entities) {
                field.set(entity, newValue);  // 更新字段值
            }
            repository.saveAll(entities);  // 保存所有更新后的实体
            return entities.size();  // 返回更新的实体数量
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("访问字段失败: " + fieldName, e);
        }
    }

    /**
     * 批量更新指定实体列表中的多个字段。
     *
     * @param ids            要更新的实体的 ID 列表
     * @param fieldsToUpdate 字段名与新值的映射
     * @return 更新的实体数量
     * @throws EntityNotFoundException  如果未找到任何实体
     * @throws IllegalArgumentException 如果指定的字段不存在
     * @throws IllegalStateException    如果无法访问字段
     */

    @Override
    @Transactional
    public Integer updateFieldsBatch(List<ID> ids, Map<String, Object> fieldsToUpdate) {
        // 从仓库中批量查找实体
        List<Entity> entities = repository.findAllById(ids);
        if (entities.isEmpty()) {
            throw new EntityNotFoundException("实体未找到");
        }

        // 获取实体类的 Class 对象
        Class<?> entityClass = entities.get(0).getClass();

        // 存储所有需要更新的 Field 对象
        Map<String, Field> fieldMap = new HashMap<>();

        // 尝试查找所有指定的字段
        for (String fieldName : fieldsToUpdate.keySet()) {
            try {
                Field field = findField(entityClass, fieldName);
                fieldMap.put(fieldName, field);
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("未找到字段: " + fieldName, e);
            }
        }

        try {
            // 遍历每个实体并更新相应的字段
            for (Entity entity : entities) {
                for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
                    Field field = fieldMap.get(entry.getKey());
                    field.set(entity, entry.getValue());
                }
            }

            // 保存所有更新后的实体
            repository.saveAll(entities);
            return entities.size(); // 返回更新的实体数量
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("访问字段失败", e);
        }
    }

    /**
     * 根据ID删除实体。
     *
     * @param id 要删除的实体的ID
     */
    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    /**
     * 检查实体类是否有软删除字段。
     *
     * @param clazz     实体类的Class对象
     * @param fieldName 软删除字段名
     * @return 如果实体类有该字段，返回true；否则，返回false
     */
    private boolean checkIfFieldExists(Class<Entity> clazz, String fieldName) {
        if (!StringUtils.hasText(fieldName)) {
            return false;
        }
        try {
            findField(clazz, fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * 查找指定类及其父类中的字段。
     *
     * @param clazz     类对象
     * @param fieldName 字段名
     * @return 字段对象
     * @throws NoSuchFieldException 如果找不到字段
     */
    public Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = null;
        while (clazz != null) {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();  // 如果当前类没有该字段，检查父类
            }
        }
        throw new NoSuchFieldException("Field not found: " + fieldName);
    }

    /**
     * 尝试设置实体的指定字段值，如果字段存在。
     *
     * @param entity    实体对象
     * @param fieldName 字段名
     * @param value     要设置的值
     */
    public void setFieldIfExists(Entity entity, String fieldName, Object value) {
        try {
            Field field = findField(entity.getClass(), fieldName);
            field.setAccessible(true);
            field.set(entity, value);
        } catch (NoSuchFieldException e) {
            // 字段不存在，忽略
        } catch (IllegalAccessException e) {
            throw new RuntimeException("访问字段失败: " + fieldName, e);
        }
    }

    /**
     * 创建分页对象。
     *
     * @param params 包含分页信息的参数Map
     * @param sort   可选的排序参数，如果没有排序要求可传入null
     * @return 返回构造的 Pageable 对象
     */
    private Pageable createPageable(Map<String, Object> params, Sort sort) {
        int page = Optional.ofNullable(params.get("page"))
                .map(Object::toString)
                .map(Integer::parseInt)
                .orElse(0) - 1;
        int rows = Optional.ofNullable(params.get("rows"))
                .map(Object::toString)
                .map(Integer::parseInt)
                .orElse(10);

        page = Math.max(page, 0);

        if (sort != null) {
            return PageRequest.of(page, rows, sort);
        } else {
            return PageRequest.of(page, rows);
        }
    }

    /**
     * 根据查询参数生成JPA规范。
     *
     * @param params 查询参数
     * @return 返回满足条件的JPA规范
     */
    private Specification<Entity> getSpecification(Map<String, Object> params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            params.forEach((key, value) ->
                    addPredicate(predicates, query, root, criteriaBuilder, key, value, params)
            );

            // 调用动态添加 GROUP BY 的方法
            addGroupBy(query, criteriaBuilder, root, params);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 根据传入的查询键和值，动态生成 JPA 谓词并加入到谓词列表。
     *
     * 支持以下特性：
     * - 内置键：`id`（IN/等值）、`creatorId`、软删除标识（当实体支持软删除时）
     * - 注解驱动：若实体字段标注了 {@link com.dqjq.base.common.lee.query.QueryField}，
     *   将按其 {@code operator/ignoreCase/alias} 配置生成条件；未标注则默认 EQ 精确匹配
     * - 键匹配：既支持实体字段名，也支持 `@Column(name)` 指定的列名（大小写不敏感）
     * - 支持的操作符：EQ, LIKE, IN, GT, GE, LT, LE, BETWEEN, STARTS_WITH, ENDS_WITH
     *
     * @param predicates      谓词列表
     * @param query           CriteriaQuery，用于创建子查询
     * @param root            实体的根引用
     * @param criteriaBuilder 条件构造器
     * @param key             查询键（字段名或列名）
     * @param value           查询值（不同操作符对值的类型要求不同）
     * @param params          其他参数
     */
    @Override
    public void addPredicate(List<Predicate> predicates, CriteriaQuery<?> query, Root<Entity> root, CriteriaBuilder criteriaBuilder, String key, Object value, Map<String, Object> params) {
        if (value == null) {
            return;
        }
        String baseKey = key;
        QueryOperator overrideOp = null;
        boolean hintIgnoreCase = false;
        boolean strict = false;
        if (params != null && params.containsKey("strict")) {
            Object s = params.get("strict");
            if (s instanceof Boolean) {
                strict = (Boolean) s;
            } else if (s != null) {
                strict = Boolean.parseBoolean(s.toString());
            }
        }
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
        switch (baseKey) {
            case "id":
                if (value instanceof List) {
                    CriteriaBuilder.In<Object> inClause = criteriaBuilder.in(root.get("id"));
                    for (Object id : (List<?>) value) {
                        inClause.value(id);
                    }
                    predicates.add(inClause);
                } else {
                    predicates.add(criteriaBuilder.equal(root.get("id"), value));
                }
                return;
            case "creatorId":
                predicates.add(criteriaBuilder.equal(root.get("creatorId"), value));
                return;
            default:
                if (hasSoftDeleteField && baseKey.equals(isDeleteFieldName)) {
                    predicates.add(criteriaBuilder.equal(root.get(isDeleteFieldName), value));
                    return;
                }
        }

        // 命名空间路径支持：RemoteSimpleName.field → EXISTS 子查询（逻辑关联，不依赖外键）
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
                Predicate bridge = criteriaBuilder.equal(rr.get(matched.remoteKey()), root.get(matched.localKey()));

                QueryDefaults qd = entityClass.getAnnotation(QueryDefaults.class);
                Predicate remoteCond = buildOperatorPredicate(criteriaBuilder, rr.get(remoteField), (overrideOp != null ? overrideOp : (qd != null ? qd.operator() : QueryOperator.EQ)), hintIgnoreCase || (qd != null && qd.ignoreCase()), value, qd);

                if (remoteCond != null) {
                    sub.select(rr.get(matched.remoteKey())).where(criteriaBuilder.and(bridge, remoteCond));
                } else {
                    sub.select(rr.get(matched.remoteKey())).where(bridge);
                }
                predicates.add(criteriaBuilder.exists(sub));
                return;
            }
        }

        try {
            Field field;
            String fieldName = baseKey;
            try {
                field = findField(entityClass, baseKey);
            } catch (NoSuchFieldException nf) {
                field = null;
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
            Predicate p = buildOperatorPredicate(criteriaBuilder, root.get(fieldName), op, ignoreCase, value, qd);
            if (p != null) {
                predicates.add(p);
            }
        } catch (SecurityException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    private Predicate buildOperatorPredicate(CriteriaBuilder cb,
                                             Path<?> path,
                                             QueryOperator op,
                                             boolean ignoreCaseHint,
                                             Object value,
                                             QueryDefaults qd) {
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


    /**
     * 辅助方法，动态添加 GROUP BY 支持。
     *
     * @param query  查询对象
     * @param root   实体根引用
     * @param params 查询参数
     */
    protected void addGroupBy(CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Root<Entity> root, Map<String, Object> params) {
        if (params.containsKey("groupByFields")) {
            // 处理 groupByFields 参数（逗号分隔的字符串）
            Object groupByFields = params.get("groupByFields");
            if (groupByFields instanceof String) {
                String fieldsStr = (String) groupByFields;
                List<Expression<?>> groupByExpressions = new ArrayList<>();
                // 逗号分隔处理
                for (String field : fieldsStr.split(",")) {
                    groupByExpressions.add(root.get(field.trim()));
                }
                query.groupBy(groupByExpressions);
            }
        } else if (params.containsKey("groupByField")) {
            // 处理单字段的 groupByField 参数
            Object groupByField = params.get("groupByField");
            if (groupByField instanceof String) {
                query.groupBy(root.get((String) groupByField));
            }
        }
    }
}
