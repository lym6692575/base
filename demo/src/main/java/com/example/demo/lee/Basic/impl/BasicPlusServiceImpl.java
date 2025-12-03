package com.example.demo.lee.Basic.impl;

import com.example.demo.lee.ResponseData;
import com.example.demo.lee.ResponseMsg;
import com.example.demo.lee.dto.userInfoDtoWithIdList;
import com.example.demo.lee.mapper.BaseMapper;
import com.example.demo.lee.repository.BasicRepository;
import com.example.demo.lee.Basic.BasicPlusService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 提供具体实体和DTO转换，分页查询及软删除功能的服务实现。
 *
 * @param <Entity> 实体类类型
 * @param <Dto>    数据传输对象类型
 * @param <ID>     实体类ID的类型
 */

public abstract class BasicPlusServiceImpl<Entity, Dto, ID> extends BasicServiceImpl<Entity, ID> implements BasicPlusService<Entity, Dto, ID> {
    private final BaseMapper<Dto, Entity> baseMapper;
    private final BasicRepository<Entity, ID> repository;
    private static final String DTO_CONVERT_PARAM = "DTO_ONLY";
    @PersistenceContext
    private EntityManager entityManager;

    public BasicPlusServiceImpl(
            BasicRepository<Entity, ID> repository,
            BaseMapper<Dto, Entity> baseMapper,
            String isDeleteFieldName,
            Object notDeletedValue,
            Object deletedValue,
            Class<Entity> entityClass
    ) {
        super(repository, isDeleteFieldName, notDeletedValue, deletedValue, entityClass);
        this.repository = repository;
        this.baseMapper = baseMapper;
    }

    public BasicPlusServiceImpl(
            BasicRepository<Entity, ID> repository,
            BaseMapper<Dto, Entity> baseMapper,
            Class<Entity> entityClass
    ) {
        super(repository, entityClass);
        this.repository = repository;
        this.baseMapper = baseMapper;
    }

    /**
     * 保存DTO到数据库，首先将DTO转换为实体对象。
     *
     * @param dto         DTO对象
     * @param entityClass 实体类的Class对象，指定转换目标类型
     */
    @Override
    @Transactional
    public ResponseData<Void> saveDto(Dto dto, Class<Entity> entityClass) {
        try {
            Entity entity = baseMapper.dtoToEntity(dto, entityClass);
            saveEntity(entity);
            return ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.SAVE_FAIL, e);
        }
    }

    /**
     * 保存DTO到数据库，首先将DTO转换为实体对象。
     *
     * @param dto         DTO对象
     * @param entityClass 实体类的Class对象，指定转换目标类型
     */
    @Override
    @Transactional
    public Entity saveDtoWithoutResponseData(Dto dto, Class<Entity> entityClass) {
        try {
            Entity entity = baseMapper.dtoToEntity(dto, entityClass);
            return saveEntity(entity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * 批量保存DTO到数据库，首先将DTO转换为实体对象。
     *
     * @param dtos        DTO对象列表
     * @param entityClass 实体类的Class对象，指定转换目标类型
     */
    @Override
    @Transactional
    public ResponseData<Void> saveDtoBatch(List<Dto> dtos, Class<Entity> entityClass) {
        try {
            // 检查输入是否为空
            if (dtos == null || dtos.isEmpty()) {
                return ResponseData.getFail(ResponseMsg.SAVE_FAIL);
            }

            // 将DTO列表转换为实体列表
            List<Entity> entities = dtos.stream()
                    .map(dto -> baseMapper.dtoToEntity(dto, entityClass))
                    .collect(Collectors.toList());

            // 批量保存实体到数据库
            saveAllEntity(entities);

            return ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.SAVE_FAIL, e);
        }
    }

    /**
     * 根据给定的参数进行分页查询，并返回DTO的分页列表。
     *
     * @param params   查询参数
     * @param dtoClass DTO的Class对象，用于确定输出的DTO类型
     * @return DTO的分页数据
     */
    @Override
    public ResponseData<Page<Dto>> findDtoByPage(Map<String, Object> params, Class<Dto> dtoClass) {
        try {
            Page<Entity> result = findEntityByPage(params);
            Page<Dto> dtoList = result.map(entity -> baseMapper.entityToDto(entity, dtoClass, params));
            return ResponseData.getSuccess(ResponseMsg.QUERY_SUCCESS, dtoList);
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.QUERY_FAIL, e);
        }
    }

    /**
     * 根据给定的参数进行查询，并返回DTO列表。
     *
     * @param params   查询参数
     * @param dtoClass DTO的Class对象，用于确定输出的DTO类型
     * @return DTO数据
     */
    @Override
    public ResponseData<List<Dto>> findDtoByList(Map<String, Object> params, Class<Dto> dtoClass) {
        try {
            List<Entity> result = findAllEntity(params);
            List<Dto> dtoList = (result == null) ? new ArrayList<>() :
                    result.stream()
                            .map(entity -> baseMapper.entityToDto(entity, dtoClass, params))
                            .collect(Collectors.toList());
            return ResponseData.getSuccess(ResponseMsg.QUERY_SUCCESS, dtoList);
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.QUERY_FAIL, e);
        }
    }

    @Override
    public List<Dto> findDtoByListWithoutResponseData(Map<String, Object> params, Class<Dto> dtoClass) {
        try {
            List<Entity> result = findAllEntity(params);
//            return (result == null) ? new ArrayList<>() :
//                    result.stream()
//                            .map(entity -> baseMapper.entityToDto(entity, dtoClass, params))
//                            .collect(Collectors.toList());
            // 判断是否需要特殊DTO转换
            if (params != null && params.containsKey(DTO_CONVERT_PARAM)) {
                // 只传 entity 和 dtoClass
                return result.stream()
                        .map(entity -> baseMapper.entityToDto(entity, dtoClass))
                        .collect(Collectors.toList());
            } else {
                // 传 entity, dtoClass, params
                return result.stream()
                        .map(entity -> baseMapper.entityToDto(entity, dtoClass, params))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Dto> findDtoByListAndSortWithoutResponseData(Map<String, Object> params, Sort sort, Class<Dto> dtoClass) {
        try {
            Page<Entity> result = findEntityByPageAndSort(params, sort);
            return (result == null) ? new ArrayList<>() :
                    result.stream()
                            .map(entity -> baseMapper.entityToDto(entity, dtoClass, params))
                            .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * 根据ID查找对应的数据实体，并转换为指定的DTO类。
     * 此方法首先尝试从数据库中查找与给定ID匹配的实体。如果找到实体，它会使用提供的映射函数将实体转换为DTO并返回成功的响应数据。
     * 如果没有找到实体，或者在处理过程中发生异常，将返回错误的响应数据。
     *
     * @param id       要查找的实体的ID。
     * @param dtoClass DTO类的Class对象，用于指定转换的目标类型。
     * @return ResponseData<Dto> 根据操作结果返回包含DTO或错误信息的响应数据对象。
     */
    @Override
    public ResponseData<Dto> findDtoById(ID id, Class<Dto> dtoClass) {
        try {
            Optional<Entity> optional = findById(id);
            return optional.map(entity -> ResponseData.getSuccess(ResponseMsg.QUERY_SUCCESS, baseMapper.entityToDto(entity, dtoClass))).orElseGet(() -> ResponseData.getError(ResponseMsg.QUERY_FAIL));
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.QUERY_FAIL, e);
        }
    }

    ;

    @Override
    public Optional<Dto> findDtoByIdWithoutResponseData(ID id, Class<Dto> dtoClass) {
        try {
            return findById(id)
                    .map(entity -> baseMapper.entityToDto(entity, dtoClass));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 根据给定的参数和排序方式进行分页查询，并返回DTO的分页列表。
     *
     * @param params   查询参数
     * @param sort     排序方式
     * @param dtoClass DTO的Class对象，用于确定输出的DTO类型
     * @return DTO的分页数据
     */
    @Override
    public ResponseData<Page<Dto>> findDtoByPageAndSort(Map<String, Object> params, Sort sort, Class<Dto> dtoClass) {
        try {
            Page<Entity> result = findEntityByPageAndSort(params, sort);
            Page<Dto> dtoList = result.map(entity -> baseMapper.entityToDto(entity, dtoClass, params));
            return ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS, dtoList);
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.QUERY_FAIL, e);
        }
    }

    public Page<Dto> findDtoByPageAndSortWithoutResponseData(Map<String, Object> params, Sort sort, Class<Dto> dtoClass) {
        try {
            Page<Entity> result = findEntityByPageAndSort(params, sort);
            return result.map(entity -> baseMapper.entityToDto(entity, dtoClass, params));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
//            return Page.empty();
        }
    }

    /**
     * 根据查询条件求某个字段的总和（字段必须是可求和的数值类型）。
     *
     * @param params      查询参数，包含查询条件，page/rows会被排除
     * @param sumField    要求和的字段名
     * @param entityClass 实体类
     * @return 求和结果
     */
    @Override
    public ResponseData<BigDecimal> sumFieldByParams(
            Map<String, Object> params,
            String sumField,
            Class<Entity> entityClass) {
//        // 1. 去掉分页参数
//        Map<String,Object> qp = new HashMap<>(params);
//        qp.remove("page");
//        qp.remove("rows");

        // 2. 拿到所有符合条件的实体
        List<Entity> list = findAllEntity(params);

        // 3. 用反射把 sumField 值取出来，再累加
        Field f;
        try {
            f = entityClass.getDeclaredField(sumField);
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            return ResponseData.getError("字段不存在: " + sumField, e);
        }

        BigDecimal sum = list.stream()
                .map(e -> {
                    try {
                        Object v = f.get(e);
                        return (v != null)
                                ? new BigDecimal(v.toString())
                                : BigDecimal.ZERO;
                    } catch (IllegalAccessException ex) {
                        return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseData.getSuccess(ResponseMsg.QUERY_SUCCESS, sum);
    }

    /**
     * 更新实体的特定字段。
     *
     * @param id        实体的ID
     * @param fieldName 要更新的字段名
     * @param newValue  新的值
     * @return 返回结果类ResponseData
     */
    @Override
    @Transactional
    public ResponseData<Entity> updateFieldReturnResponseClass(ID id, String fieldName, Object newValue) {
        try {
            Entity entity = updateField(id, fieldName, newValue);
            return ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS, entity);
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.COMMON_FAIL);
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
    public ResponseData<Integer> updateFieldBatchReturnResponseClass(List<ID> ids, String fieldName, Object newValue) {
        try {
            Integer count = updateFieldBatch(ids, fieldName, newValue);
            return ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS, count);
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.COMMON_FAIL, e);
        }
    }

    /**
     * 批量更新实体的特定字段。
     *
     * @param ids            实体ID列表
     * @param fieldsToUpdate 要更新的字段名
     * @return 更新的实体数量
     */
    @Override
    @Transactional
    public ResponseData<Integer> updateFieldsBatchReturnResponseClass(List<ID> ids, Map<String, Object> fieldsToUpdate) {
        try {
            Integer count = updateFieldsBatch(ids, fieldsToUpdate);
            return ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS, count);
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.COMMON_FAIL, e);
        }
    }

    /**
     * 执行单个实体的软删除操作，附带记录执行删除操作的用户信息。
     *
     * @param id       实体的ID
     * @param userId   执行删除操作的用户ID
     * @param userName 执行删除操作的用户名
     */
    @Override
    @Transactional
    public void softDelete(ID id, String userId, String userName) {
        Optional<Entity> optionalEntity = findById(id);
        if (optionalEntity.isPresent()) {
            Entity entity = optionalEntity.get();
            try {
                Field isDeleteField = findField(entity.getClass(), super.getIsDeleteFieldName());
                isDeleteField.setAccessible(true);
                Object currentValue = isDeleteField.get(entity);
                if (!super.getDeletedValue().equals(currentValue)) { // 检查是否已被删除
                    isDeleteField.set(entity, super.getDeletedValue()); // 设置已删除值

                    // 尝试设置删除人信息
                    setFieldIfExists(entity, "deletedId", userId);
                    setFieldIfExists(entity, "deletedName", userName);
                    setFieldIfExists(entity, "deletedAt", LocalDateTime.now());

                    saveEntity(entity);
                } else {
                    throw new RuntimeException("实体已被删除");
                }
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("该实体不支持软删除", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("访问字段失败: " + super.getIsDeleteFieldName(), e);
            }
        } else {
            throw new RuntimeException("未找到实体");
        }
    }

    /**
     * 批量软删除指定ID列表中的实体，并记录执行删除操作的用户信息。
     *
     * @param idsList  实体ID列表
     * @param userId   执行删除操作的用户ID
     * @param userName 执行删除操作的用户名
     */
    @Override
    @Transactional
    public void batchSoftDelete(List<ID> idsList, String userId, String userName) {
        List<Entity> entities = repository.findAllById(idsList);

        for (Entity entity : entities) {
            try {
                Field isDeleteField = findField(entity.getClass(), super.getIsDeleteFieldName());
                isDeleteField.setAccessible(true);
                Object currentValue = isDeleteField.get(entity);
                if (!super.getDeletedValue().equals(currentValue)) { // 检查是否已被删除
                    isDeleteField.set(entity, super.getDeletedValue()); // 设置已删除值

                    // 尝试设置删除人信息
                    setFieldIfExists(entity, "deletedId", userId);
                    setFieldIfExists(entity, "deletedName", userName);
                    setFieldIfExists(entity, "deletedAt", LocalDateTime.now());
                } else {
                    throw new RuntimeException("某些实体已经被删除");
                }
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("某些实体不支持软删除", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("访问字段失败: " + super.getIsDeleteFieldName(), e);
            }
        }

        repository.saveAll(entities);
    }

    @Override
    @Transactional
    public ResponseData<Void> batchSoftDeleteReturnResponseClass(userInfoDtoWithIdList<ID> dto) {
        try {
            batchSoftDelete(dto.getIdsList(), dto.getUserId(), dto.getUserName());
            return ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.DELETE_FAIL, e);
        }
    }
}