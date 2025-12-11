package com.example.demo.common.lee.component;

import com.example.demo.common.lee.ResponseData;
import com.example.demo.common.lee.ResponseMsg;
import com.example.demo.common.lee.dto.userInfoDtoWithIdList;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 删除特性 Trait
 * <p>
 * 提供物理删除和软删除逻辑。
 * </p>
 */
public interface DeleteTrait<E, D, ID> extends BaseTrait<E, D, ID> {

    /**
     * 物理删除
     */
    default void deleteById(ID id) {
        getRepository().deleteById(id);
    }

    /**
     * 软删除
     */
    default void softDelete(ID id, String userId, String userName) {
        Optional<E> optionalEntity = getRepository().findById(id);
        if (optionalEntity.isPresent()) {
            E entity = optionalEntity.get();
            performSoftDelete(entity, userId, userName);
            getRepository().save(entity);
        } else {
            throw new RuntimeException("未找到实体");
        }
    }

    /**
     * 批量软删除
     */
    default void batchSoftDelete(List<ID> ids, String userId, String userName) {
        List<E> entities = getRepository().findAllById(ids);
        for (E entity : entities) {
            performSoftDelete(entity, userId, userName);
        }
        getRepository().saveAll(entities);
    }

    default ResponseData<Void> batchSoftDeleteReturnResponseClass(userInfoDtoWithIdList<ID> dto) {
        try {
            batchSoftDelete(dto.getIdsList(), dto.getUserId(), dto.getUserName());
            return ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.DELETE_FAIL, e);
        }
    }

    // --- 内部辅助方法 ---

    default void performSoftDelete(E entity, String userId, String userName) {
        try {
            // 假设软删除字段固定为 "isDelete"，值为 1
            // 实际项目中应从配置或注解读取，这里为了简化直接硬编码演示
            // 或者通过 getIsDeleteFieldName() 接口方法获取
            String fieldName = "isDelete";
            Object deletedVal = 1;
            Object notDeletedVal = 0;

            Field isDeleteField = findField(entity.getClass(), fieldName);
            isDeleteField.setAccessible(true);
            Object currentValue = isDeleteField.get(entity);
            
            if (!deletedVal.equals(currentValue)) {
                isDeleteField.set(entity, deletedVal);
                setFieldIfExists(entity, "deletedId", userId);
                setFieldIfExists(entity, "deletedName", userName);
                setFieldIfExists(entity, "deletedAt", LocalDateTime.now());
            } else {
                throw new RuntimeException("实体已被删除");
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("该实体不支持软删除", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("访问字段失败", e);
        }
    }

    default Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    default void setFieldIfExists(Object entity, String fieldName, Object value) {
        try {
            Field field = findField(entity.getClass(), fieldName);
            field.set(entity, value);
        } catch (Exception e) {
            // ignore
        }
    }
}
