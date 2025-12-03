package {{package}};

import com.dqjq.base.common.lee.mapper.BaseMapper;
import {{dtoPackage}}.{{dtoName}};
import {{entityPackage}}.{{entityName}}Entity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class {{entityName}}Mapper implements BaseMapper<{{dtoName}}, {{entityName}}Entity> {
    private final ModelMapper modelMapper;

    public {{entityName}}Mapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public {{entityName}}Entity dtoToEntity({{dtoName}} dto, Class<{{entityName}}Entity> entityType) {
        return modelMapper.map(dto, entityType);
    }

    @Override
    public {{dtoName}} entityToDto({{entityName}}Entity entity, Class<{{dtoName}}> dtoType) {
        return modelMapper.map(entity, dtoType);
    }
}