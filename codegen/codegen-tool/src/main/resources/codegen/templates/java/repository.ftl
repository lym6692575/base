package ${package};

import com.dqjq.base.common.lee.repository.BasicRepository;
import ${entityPackage}.${entityName}Entity;

@Repository
public interface ${entityName}Repo extends BasicRepository<${entityName}Entity, ${idType}> {
}
