package com.example.demo.app.pwlpz.dto;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestEntityDto {

    @ApiModelProperty(value="主键")
    private String id;

    @ApiModelProperty(value="名称")
    private String name;

}