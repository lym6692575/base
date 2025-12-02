package com.example.demo.lee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class userInfoDtoWithIdList<ID> {
    private String userId;
    private String userName;
    private List<ID> idsList;
}