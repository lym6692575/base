package com.example.demo.app.test.entity;

@Entity
@Table(name = "t_d_test")
@Data
public class TDtestEntity {

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