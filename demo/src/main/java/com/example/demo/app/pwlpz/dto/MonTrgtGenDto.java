package com.example.demo.app.pwlpz.dto;

import lombok.Data;
import com.example.demo.common.lee.dto.BaseDto;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModelProperty;


@lombok.Data
@lombok.EqualsAndHashCode(callSuper=false)
public class MonTrgtGenDto extends BaseDto {
    @io.swagger.annotations.ApiModelProperty(value = "trgtName")
    private String trgtName;

    @io.swagger.annotations.ApiModelProperty(value = "plantName")
    private String plantName;

    @io.swagger.annotations.ApiModelProperty(value = "activeChk")
    private String activeChk;

}
