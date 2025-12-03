package {{package}};

import com.dqjq.base.common.lee.service.basic.basicService.BasicPlusService;
import {{dtoPackage}}.{{dtoName}};
import {{entityPackage}}.{{entityName}}Entity;

public interface {{entityName}}Service extends BasicPlusService<{{entityName}}Entity, {{dtoName}}, {{idType}}> {
}