package com.dqjq.codegen.Generator;

import com.dqjq.codegen.CodegenConfig;
import com.dqjq.codegen.Generator.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 代码生成器入口
 * <p>
 * 协调各个子生成器（Entity, DTO, Repository, etc.）执行生成任务。
 * </p>
 */
public class CodeGenerator {
    final CodegenConfig cfg;
    final Path projectRoot = Paths.get("");
    Path mainEntityPath;

    public CodeGenerator(CodegenConfig cfg) {
        this.cfg = cfg;
    }

    public void generate() throws IOException {
        // 1. 生成 Entity
        EntityGenerator entityGen = new EntityGenerator(cfg);
        entityGen.generate();
        this.mainEntityPath = entityGen.getEntityPath();

//        // 2. 生成 DTO
//        new DtoGenerator(cfg).generate();
//
//        // 3. 生成 Repository
//        new RepositoryGenerator(cfg).generate();
//
//        // 4. 生成 Mapper
//        new MapperGenerator(cfg).generate();
//
//        // 5. 生成 Service
//        new ServiceGenerator(cfg).generate();
//
//        // 6. 生成 ServiceImpl
//        new ServiceImplGenerator(cfg).generate();
    }
    
    public Path getMainEntityPath() {
        return projectRoot.resolve(mainEntityPath);
    }
}
