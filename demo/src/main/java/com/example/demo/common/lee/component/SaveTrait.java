package com.example.demo.common.lee.component;

import com.example.demo.common.lee.ResponseData;
import com.example.demo.common.lee.ResponseMsg;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 保存特性 Trait
 * <p>
 * 提供保存、批量保存等写操作逻辑。
 * </p>
 */
public interface SaveTrait<E, D, ID> extends BaseTrait<E, D, ID> {

    /**
     * 保存 DTO
     */
    default ResponseData<Void> saveDto(D dto) {
        try {
            E entity = getMapper().dtoToEntity(dto);
            getRepository().save(entity);
            return ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.SAVE_FAIL, e);
        }
    }

    /**
     * 保存 DTO 并返回 Entity
     */
    default E saveDtoWithoutResponseData(D dto) {
        try {
            E entity = getMapper().dtoToEntity(dto);
            return getRepository().save(entity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 批量保存 DTO
     */
    default ResponseData<Void> saveDtoBatch(List<D> dtos) {
        try {
            if (dtos == null || dtos.isEmpty()) {
                return ResponseData.getFail(ResponseMsg.SAVE_FAIL);
            }
            List<E> entities = dtos.stream()
                    .map(d -> getMapper().dtoToEntity(d))
                    .collect(Collectors.toList());
            getRepository().saveAll(entities);
            return ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception e) {
            return ResponseData.getError(ResponseMsg.SAVE_FAIL, e);
        }
    }
}
