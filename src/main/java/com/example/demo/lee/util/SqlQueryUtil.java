package com.example.demo.lee.util;

import com.dqjq.base.common.lee.entity.BaseSqlMapper;
import com.dqjq.base.common.lee.repository.BaseSqlMapperRepository;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于 BaseSqlMapper 配置执行 SQL 查询的工具类。
 */
public class SqlQueryUtil {

    /**
     * 根据提供的 mappingKey 和参数执行查询。
     *
     * @param mapperRepository 用于获取 BaseSqlMapper 配置的仓库。
     * @param dsl              用于构建和执行 SQL 查询的 DSLContext。
     * @param mappingKey       用于标识 SQL 映射配置的键。
     * @param params           绑定到 SQL 查询占位符的参数。
     * @return 查询结果的列表，每个结果以 Map 形式表示。
     * @throws IllegalArgumentException 如果 mappingKey 无效或配置不匹配。
     */
    public static List<Map<String, Object>> queryData(
            BaseSqlMapperRepository mapperRepository,
            DSLContext dsl,
            String mappingKey,
            Object... params) {

        // 根据 mappingKey 获取映射配置
        BaseSqlMapper mapper = mapperRepository.findByMappingKey(mappingKey);
        if (mapper == null) {
            throw new IllegalArgumentException("无效的 mappingKey: " + mappingKey);
        }

        // 确保映射类型为 "QUERY"
        if (!"QUERY".equalsIgnoreCase(mapper.getType())) {
            throw new IllegalArgumentException("映射类型不是 QUERY，当前类型: " + mapper.getType());
        }

        // 动态创建表引用
        Table<?> table = DSL.table(DSL.name(mapper.getTableName()));

        // 解析并准备选择的列
        String selectColumnsStr = mapper.getSelectColumns(); // 例如 "code as id,name"
        if (selectColumnsStr == null || selectColumnsStr.trim().isEmpty()) {
            throw new IllegalArgumentException("未指定要查询的列。");
        }

        String[] columns = selectColumnsStr.split(",");
        List<Field<?>> fields = parseSelectColumns(columns);

        // 使用绑定参数构建 WHERE 条件
        Condition whereCondition;
        try {
            whereCondition = DSL.condition(mapper.getConditions(), params);
        } catch (Exception e) {
            throw new IllegalArgumentException("绑定参数时出错。请检查条件和参数是否匹配。", e);
        }

        // 执行查询并获取结果
        List<Map<String, Object>> results = dsl.select(fields)
                .from(table)
                .where(whereCondition)
                .fetchMaps();

        return results;
    }

    /**
     * 解析选择的列并处理可能存在的别名。
     *
     * @param columns 列数组，例如 ["code as id", "name"]
     * @return 处理后的 jOOQ Field 列表。
     */
    private static List<Field<?>> parseSelectColumns(String[] columns) {
        List<Field<?>> fields = new ArrayList<>();
        for (String column : columns) {
            String trimmedColumn = column.trim();
            if (trimmedColumn.toLowerCase().contains(" as ")) {
                String[] parts = trimmedColumn.split("(?i)\\s+as\\s+"); // 忽略大小写分割
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
