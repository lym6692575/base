package com.example.demo.lee.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDto {
    private String id; // id
    private String creatorId; // 创建人id
    @ExcelProperty(value = "创建人")
    private String creatorName; // 创建人姓名
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createdAt; // 创建日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate; // 创建日期
    private String updatedId; // 修改人id
    private String updatedName; // 修改人姓名
    private LocalDateTime updatedAt; // 修改时间
    private String deletedId; // 删除人id
    private String deletedName; // 删除人姓名
    private LocalDateTime deletedAt; // 删除时间
}
