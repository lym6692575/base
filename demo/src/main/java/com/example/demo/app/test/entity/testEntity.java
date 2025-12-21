package com.example.demo.app.test.entity;

import lombok.*;
import javax.persistence.*;
import io.swagger.annotations.ApiModelProperty;
import com.example.demo.common.lee.entity.BaseEntity;

/**
 * 业务实体类: test
 */
@Entity
@Table(name = "test")
@Data
@EqualsAndHashCode(callSuper = true)
public class testEntity extends BaseEntity {

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