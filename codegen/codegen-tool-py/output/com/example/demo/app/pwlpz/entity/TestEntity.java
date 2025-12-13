package com.example.demo.app.pwlpz.entity;

@Entity
@Table(name = "test_table")
@Data
public class TestEntity {

    @ApiModelProperty(value = "主键")
    @Id
    @Column(name = "ID")
    private String id;

    @ApiModelProperty(value = "名称")
    @Column(name = "NAME")
    private String name;

}