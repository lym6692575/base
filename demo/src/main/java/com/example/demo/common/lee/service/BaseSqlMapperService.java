package com.example.demo.common.lee.service;


import com.example.demo.common.lee.entity.BaseSqlMapper;
import com.example.demo.common.lee.repository.BaseSqlMapperRepository;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BaseSqlMapperService {

    private final BaseSqlMapperRepository mapperRepository;
    private final DSLContext primaryDsl;

    public BaseSqlMapperService(BaseSqlMapperRepository mapperRepository, DSLContext primaryDsl) {
        this.mapperRepository = mapperRepository;
        this.primaryDsl = primaryDsl;
    }

    /**
     * 根据映射键获取下拉框数据
     *
     * @param mappingKey 映射键，例如 "LLJ"
     * @return 下拉框数据列表
     */
    public List<Map<String, Object>> getDropdownData(String mappingKey, boolean useSecondary) {
        DSLContext dsl = primaryDsl;
        BaseSqlMapper mapper = mapperRepository.findByMappingKey(mappingKey);
        if (mapper == null) {
            throw new IllegalArgumentException("无效的 mappingKey: " + mappingKey);
        }

        // 检查 mapper 的 type 是否为 "SELECT"
        if (!"SELECT".equalsIgnoreCase(mapper.getType())) {
            throw new IllegalArgumentException("映射的类型不是 SELECT，无法查询数据");
        }

        // 创建动态表引用
        Table<?> table = DSL.table(DSL.name(mapper.getTableName()));

        // 动态创建要查询的字段列表，并处理别名
        String selectColumnsStr = mapper.getSelectColumns(); // 例如 "code as id,name"
        if (selectColumnsStr == null || selectColumnsStr.trim().isEmpty()) {
            throw new IllegalArgumentException("未指定要查询的列");
        }

        String[] columns = selectColumnsStr.split(",");
        List<Field<?>> fields = parseSelectColumns(columns);

        // 构建 WHERE 条件
        Condition whereCondition = DSL.condition(mapper.getConditions());

        // 构建查询
        SelectConditionStep<Record> query = dsl.select(fields)
                .from(table)
                .where(whereCondition);

        // 获取 SQL 字符串，并打印
        String sql = query.getSQL(ParamType.INLINED);
        System.out.println("Executing SQL: " + sql);

        // 执行查询
        List<Map<String, Object>> results = query.fetchMaps();

        return results;
    }

    /**
     * 根据映射键和参数获取下拉框数据
     *
     * @param mappingKey 映射键，例如 "LLJ"
     * @param params     查询参数，用于绑定到 SQL 中的占位符
     * @return 下拉框数据列表
     */
    public List<Map<String, Object>> getDropdownDataWithParams(String mappingKey, boolean useSecondary, Object... params) {
        DSLContext dsl = primaryDsl;
        BaseSqlMapper mapper = mapperRepository.findByMappingKey(mappingKey);
        if (mapper == null) {
            throw new IllegalArgumentException("无效的 mappingKey: " + mappingKey);
        }

        // 检查 mapper 的 type 是否为 "SELECT_P"
        if (!"SELECT_P".equalsIgnoreCase(mapper.getType())) {
            throw new IllegalArgumentException("映射的类型不是 SELECT_P，无法查询数据");
        }

        // 创建动态表引用
        Table<?> table = DSL.table(DSL.name(mapper.getTableName()));

        // 动态创建要查询的字段列表，并处理别名
        String selectColumnsStr = mapper.getSelectColumns(); // 例如 "code as id,name"
        if (selectColumnsStr == null || selectColumnsStr.trim().isEmpty()) {
            throw new IllegalArgumentException("未指定要查询的列");
        }

        String[] columns = selectColumnsStr.split(",");
        List<Field<?>> fields = parseSelectColumns(columns);

        String conditions = mapper.getConditions();

        // 构建 WHERE 条件，并绑定参数
        Condition whereCondition;
        try {
            whereCondition = DSL.condition(conditions, params);
        } catch (Exception e) {
            throw new IllegalArgumentException("绑定参数时出错，请检查条件和参数是否匹配", e);
        }

        // 构建并执行查询
        // 构建查询
        SelectConditionStep<Record> query = dsl.select(fields)
                .from(table)
                .where(whereCondition);

        // 获取 SQL 字符串，并打印
        String sql = query.getSQL(ParamType.INLINED);
        System.out.println("Executing SQL: " + sql);

        // 执行查询

        return query.fetchMaps();
    }

    /**
     * 解析 selectColumns 数组，并处理可能存在的别名
     *
     * @param columns 列数组，例如 ["code as id", "name"]
     * @return 处理后的字段列表
     */
    private List<Field<?>> parseSelectColumns(String[] columns) {
        List<Field<?>> fields = new ArrayList<>();
        for (String column : columns) {
            String trimmedColumn = column.trim();
            if (trimmedColumn.toLowerCase().contains(" as ")) {
                String[] parts = trimmedColumn.split("(?i)\\s+as\\s+"); // 使用正则忽略大小写
                if (parts.length != 2) {
                    throw new IllegalArgumentException("无效的列别名格式: " + trimmedColumn);
                }
                String columnName = parts[0].trim();
                String alias = parts[1].trim();
                fields.add(DSL.field(DSL.name(columnName)).as(alias));
            } else {
                fields.add(DSL.field(DSL.name(trimmedColumn)));
            }
        }
        return fields;
    }
}
