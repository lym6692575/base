package com.example.demo.common.lee.Basic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *  * 基础服务实现类，提供基本的数据库操作实现。
 *  * @param <Entity> 实体类类型
 *  * @param <ID> 实体类ID类型
 */
public interface BasicService<Entity, ID> {

    /**
     * 根据ID查找实体。
     * @param id 实体的ID
     * @return 返回一个包含找到的实体的Optional，如果未找到则为empty
     */
    Optional<Entity> findById(ID id);

    /**
     * 根据条件查找所有符合条件的实体。
     * @param params 查询参数
     * @return 返回满足条件的实体列表
     */
    List<Entity> findAllEntity(Map<String, Object> params);

    /**
     * 分页查询实体列表。
     * @param params 查询参数
     * @return 返回一个包含分页结果的 Page(domain) 对象
     */
    Page<Entity> findEntityByPage(Map<String, Object> params);

    /**
     * 分页并排序查询实体列表。
     * @param params 查询参数
     * @param sort(domain)  排序参数
     * @return 返回一个包含分页结果的 Page(domain)  对象
     */
    Page<Entity> findEntityByPageAndSort(Map<String, Object> params, Sort sort);

    /**
     * 更新实体的特定字段。
     *
     * @param id        实体的ID
     * @param fieldName 要更新的字段名
     * @param newValue  新的值
     * @return 更新后的实体
     */
    Entity updateField(ID id, String fieldName, Object newValue);

    /**
     * 批量更新实体的特定字段。
     *
     *
     * @param ids       实体ID列表
     * @param fieldName 要更新的字段名
     * @param newValue  新的值
     * @return 更新的实体数量
     */
    Integer updateFieldBatch(List<ID> ids, String fieldName, Object newValue);

    /**
     * 批量更新指定实体列表中的多个字段。
     *
     * @param ids           要更新的实体的 ID 列表
     * @param fieldsToUpdate 字段名与新值的映射
     * @return 更新的实体数量
     * @throws IllegalArgumentException 如果指定的字段不存在
     * @throws IllegalStateException 如果无法访问字段
     */
    public Integer updateFieldsBatch(List<ID> ids, Map<String, Object> fieldsToUpdate);
    /**
     * 保存单个实体。
     *
     * @param entity 要保存的实体
     * @return
     */
    Entity saveEntity(Entity entity);
    Entity saveEntityWithReturn(Entity entity);
    /**
     * 保存多个实体。
     * @param entities 要保存的实体列表
     */
    void saveAllEntity(List<Entity> entities);

    /**
     * 根据ID删除实体。
     * @param id 要删除的实体的ID
     */
    void deleteById(ID id);

    /**
     * 根据查询条件对指定字段求和（支持动态拼接查询条件）
     *
     * @param params      查询参数 map，key 对应 addPredicate 支持的字段
     * @param fieldName   要求和的实体字段名（例如 "weight"）
     * @param entityClass 实体类型 Class 对象，对应类上定义的 Entity 泛型
     * @return 符合条件的字段求和结果（null 会被转为 0）
     */
    BigDecimal sumByField(Map<String, Object> params,
                          String fieldName,
                          Class<Entity> entityClass);

    void addPredicate(List<Predicate> predicates,
                                CriteriaQuery<?> query,
                                Root<Entity> root,
                                CriteriaBuilder cb,
                                String key,
                                Object value,
                                Map<String, Object> params);
}
