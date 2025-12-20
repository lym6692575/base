package com.example.demo.app.test.dto;

@Data
@EqualsAndHashCode(callSuper = false)
public class TDtestEntityDto {

    @ApiModelProperty(value="trgtName")
    private String trgtName;

    @ApiModelProperty(value="plantName")
    private String plantName;

    @ApiModelProperty(value="activeChk")
    private String activeChk;

}