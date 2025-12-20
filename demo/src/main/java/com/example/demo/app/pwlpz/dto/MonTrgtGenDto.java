package com.example.demo.app.pwlpz.dto;

import com.example.demo.common.lee.dto.BaseDto;
import com.example.demo.common.lee.entity.BaseEntity;
import lombok.*;
import io.swagger.annotations.ApiModelProperty;

@Data
@EqualsAndHashCode(callSuper=false)
public class MonTrgtGenDto extends BaseDto {

    @ApiModelProperty(value="trgtName")
    private String trgtName;
    private String trgtNameTest;

    @ApiModelProperty(value="plantName")
    private String plantName;

    @ApiModelProperty(value="activeChk")
    private String activeChk;

}
