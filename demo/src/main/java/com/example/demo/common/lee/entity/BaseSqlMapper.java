package com.example.demo.common.lee.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@Table(name = "base_sql_mapper")
public class BaseSqlMapper {

    @ApiModelProperty(value = "主键id")
    @Id
    @Column(name = "id", unique = true, nullable = false, length = 50)
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @ApiModelProperty(value = "类型")
    @Column(name = "type", length = 100)
    private String type;

    @ApiModelProperty(value = "映射键")
    @Column(name = "mapping_key", length = 100)
    private String mappingKey;

    @ApiModelProperty(value = "要查询的列")
    @Column(name = "select_columns", length = 200)
    private String selectColumns;

    @ApiModelProperty(value = "查询条件")
    @Column(name = "conditions", length = 500)
    private String conditions;

    @ApiModelProperty(value = "表名")
    @Column(name = "table_name", length = 100)
    private String tableName;

    @ApiModelProperty(value = "备注")
    @Column(name = "note", length = 500)
    private String note;
}
