package ${package};

import com.example.demo.common.lee.mapper.BaseMapper;
import ${dtoPackage}.${dtoName};
import ${entityPackage}.${entityName}Entity;
import org.springframework.stereotype.Component;

/**
 * ${entityName} Mapper
 * <p>
 * 继承 BaseMapper，自动获得基础的 Entity <-> DTO 转换能力。
 * 如有特殊转换逻辑，请在此类中重写 dtoToEntity 或 entityToDto 方法。
 * </p>
 */
@Component
public class ${entityName}Mapper extends BaseMapper<${dtoName}, ${entityName}Entity> {
    
    public ${entityName}Mapper() {
        super(${dtoName}.class, ${entityName}Entity.class);
    }

    /*
    @Override
    public ${entityName}Entity dtoToEntity(${dtoName} dto) {
        // 1. 调用父类默认转换
        ${entityName}Entity entity = super.dtoToEntity(dto);
        // 2. 处理特殊逻辑
        if (entity != null) {
            // entity.setSomeField(convert(dto.getSomeField()));
        }
        return entity;
    }

    @Override
    public ${dtoName} entityToDto(${entityName}Entity entity) {
        // 1. 调用父类默认转换
        ${dtoName} dto = super.entityToDto(entity);
        // 2. 处理特殊逻辑
        if (dto != null) {
            // dto.setSomeField(convert(entity.getSomeField()));
        }
        return dto;
    }
    */
}
