package com.example.demo.app.pwlpz.entity;

import javax.persistence.Table;
import lombok.Data;
import com.example.demo.common.lee.entity.BaseEntity;
import javax.persistence.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "test")
@Data
public class MonTrgtGenEntity extends BaseEntity {

    @ApiModelProperty(value = "trgtName")
    @Column(name = "TRGT_NAME")
    private String trgtName;

    @ApiModelProperty(value = "plantName")
    @Column(name = "PLANT_NAME")
    private String plantName;

    @ApiModelProperty(value = "activeChk")
    @Column(name = "ACTIVE_CHK")
    private String activeChk;

}
