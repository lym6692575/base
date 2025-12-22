package com.example.demo.app.test.dto;

import lombok.*;
import io.swagger.annotations.ApiModelProperty;
import com.example.demo.common.lee.dto.BaseDto;

/**
 * 数据传输对象: testDto
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class testDto extends BaseDto {

    @ApiModelProperty(value = "trgtName")
    private String trgtName;

    @ApiModelProperty(value = "plantName")
    private String plantName;

    @ApiModelProperty(value = "activeChk")
    private String activeChk;

}