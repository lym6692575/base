package com.example.demo.lee.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class BaseEntity implements Serializable {

    @ApiModelProperty(value = "主键id")
    @Id
    @Column(name = "id", unique = true, nullable = false, length = 50)
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @ApiModelProperty(value = "创建人id")
    @Column(name = "creatorId", length = 50)
    private String creatorId;

    @ApiModelProperty(value = "创建人姓名")
    @Column(name = "creatorName", length = 20)
    private String creatorName;

    @ApiModelProperty(value = "创建时间", example = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "createdAt", length = 20)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "创建日期", example = "yyyy-MM-dd")
    @Column(name = "createdDate", length = 20)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @ApiModelProperty(value = "修改人id")
    @Column(name = "updatedId", length = 50)
    private String updatedId;

    @ApiModelProperty(value = "修改人姓名")
    @Column(name = "updatedName", length = 20)
    private String updatedName;

    @ApiModelProperty(value = "修改时间", example = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updatedAt", length = 20)
    private LocalDateTime updatedAt;

    @ApiModelProperty(value = "删除人id")
    @Column(name = "deletedId", length = 50)
    private String deletedId;

    @ApiModelProperty(value = "删除人姓名")
    @Column(name = "deletedName", length = 20)
    private String deletedName;

    @ApiModelProperty(value = "删除时间", example = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "deletedAt", length = 20)
    private LocalDateTime deletedAt;

    @ApiModelProperty(value = "删除标志", example = "0 or 1")
    @Column(name = "isDelete", nullable = false, columnDefinition = "int default 0")
    private Integer isDelete = 0;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.createdDate == null) {
            this.createdDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}