package com.example.demo.lee.service.basic.basicService;



import com.example.demo.lee.ResponseData;
import com.example.demo.lee.dto.userInfoDtoWithIdList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * BasicPlusService 接口，扩展了 BasicService 接口，增加了DTO处理和软删除功能。
 * @param <Entity> 实体类的类型
 * @param <Dto> DTO类的类型
 * @param <ID> 实体类ID的类型
 */
public interface BasicPlusService<Entity, Dto, ID> extends BasicService<Entity, ID> {


    /**
     * 保存DTO对象并转换为实体类。
     * @param dto 要保存的DTO对象
     * @param entityClass 实体类的类型，用于将DTO转换为实体
     */
    ResponseData<Void> saveDto(Dto dto, Class<Entity> entityClass);
    Entity saveDtoWithoutResponseData(Dto dto, Class<Entity> entityClass);


    /**
     * 批量保存DTO到数据库，首先将DTO转换为实体对象。
     *
     * @param dtos        DTO对象列表
     * @param entityClass 实体类的Class对象，指定转换目标类型
     */
    public ResponseData<Void> saveDtoBatch(List<Dto> dtos, Class<Entity> entityClass);
    /**
     * 查询并分页返回DTO列表。
     * @param params 查询参数，用于筛选和定位数据
     * @param dtoClass DTO类的类型，用于确定返回的数据类型
     * @return 返回符合条件的DTO对象的分页列表
     */
    ResponseData<Page<Dto>> findDtoByPage(Map<String, Object> params, Class<Dto> dtoClass);

    /**
     * 查询并分页返回DTO。
     * @param params 查询参数，用于筛选和定位数据
     * @param dtoClass DTO类的类型，用于确定返回的数据类型
     * @return 返回符合条件的DTO列表
     */
    ResponseData<List<Dto>> findDtoByList(Map<String, Object> params, Class<Dto> dtoClass);

    List<Dto> findDtoByListWithoutResponseData(Map<String, Object> params, Class<Dto> dtoClass);
    List<Dto> findDtoByListAndSortWithoutResponseData(Map<String, Object> params, Sort sort, Class<Dto> dtoClass);

    /**
     * 查询并分页返回DTO列表，包含排序功能。
     * @param params 查询参数，用于筛选和定位数据
     * @param sort 排序参数，定义了数据排序的方式
     * @param dtoClass DTO类的类型，用于确定返回的数据类型
     * @return 返回符合条件且经过排序的DTO对象的分页列表
     */
    ResponseData<Page<Dto>> findDtoByPageAndSort(Map<String, Object> params, Sort sort, Class<Dto> dtoClass);
    Page<Dto> findDtoByPageAndSortWithoutResponseData(Map<String, Object> params, Sort sort, Class<Dto> dtoClass);

    /**
     * 根据ID查找对应的数据实体，并转换为指定的DTO类。
     * 此方法首先尝试从数据库中查找与给定ID匹配的实体。如果找到实体，它会使用提供的映射函数将实体转换为DTO并返回成功的响应数据。
     * 如果没有找到实体，或者在处理过程中发生异常，将返回错误的响应数据。
     *
     * @param id        要查找的实体的ID。
     * @param dtoClass  DTO类的Class对象，用于指定转换的目标类型。
     * @return ResponseData<Dto> 根据操作结果返回包含DTO或错误信息的响应数据对象。
     */
    ResponseData<Dto> findDtoById(ID id, Class<Dto> dtoClass);
    Optional<Dto> findDtoByIdWithoutResponseData(ID id, Class<Dto> dtoClass);

    /**
     * 更新实体的特定字段。
     *
     * @param id        实体的ID
     * @param fieldName 要更新的字段名
     * @param newValue  新的值
     * @return 返回结果类 ResponseData<Entity>
     */
    ResponseData<Entity> updateFieldReturnResponseClass(ID id, String fieldName, Object newValue);

    /**
     * 批量更新实体的特定字段。
     *
     *
     * @param ids       实体ID列表
     * @param fieldName 要更新的字段名
     * @param newValue  新的值
     * @return 更新的实体数量
     */
    ResponseData<Integer> updateFieldBatchReturnResponseClass(List<ID> ids, String fieldName, Object newValue);

    /**
     * 批量更新实体的特定字段。
     *
     * @param ids       实体ID列表
     * @param fieldsToUpdate 要更新的字段名
     * @return 更新的实体数量
     */
    @Transactional
    ResponseData<Integer> updateFieldsBatchReturnResponseClass(List<ID> ids, Map<String, Object> fieldsToUpdate);

    /**
     * 软删除指定ID的实体。
     * @param id 要软删除的实体的ID
     * @param userId 执行软删除的用户ID
     * @param userName 执行软删除的用户名
     */
    void softDelete(ID id, String userId, String userName);

    /**
     * 批量软删除指定ID列表中的所有实体，并更新删除用户信息。
     * @param ids 要软删除的实体ID列表
     * @param userId 执行软删除的用户ID
     * @param userName 执行软删除的用户名
     */
    void batchSoftDelete(List<ID> ids, String userId, String userName);

    ResponseData<Void> batchSoftDeleteReturnResponseClass(userInfoDtoWithIdList<ID> dto);


    ResponseData<BigDecimal> sumFieldByParams(
            Map<String, Object> params,
            String sumField,
            Class<Entity> entityClass);
}
