package com.example.demo.app.pwlpz.controller;


import com.example.demo.app.pwlpz.dto.MonTrgtGenDto;
import com.example.demo.app.pwlpz.entity.MonTrgtGenEntity;
import com.example.demo.app.pwlpz.service.MonTrgtGenService;
import com.example.demo.common.lee.ResponseData;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 二级单位管理
 */

@RestController
@RequestMapping("/test")
public class TestController {
    private final MonTrgtGenService monTrgtGenService;

    public TestController(MonTrgtGenService monTrgtGenService) {
        this.monTrgtGenService = monTrgtGenService;
    }

    @ApiOperation(value = "员工信息确认及填报", notes = "查询", httpMethod = "GET")
    @GetMapping
    public ResponseData<Page<MonTrgtGenDto>> query(@RequestParam Map<String, Object> params) {
        return monTrgtGenService.findDtoByPage(params);
    }

    @ApiOperation(value = "员工信息确认及填报", notes = "存", httpMethod = "GET")
    @PostMapping
    public ResponseData<MonTrgtGenEntity> save(@RequestBody MonTrgtGenDto dto) {
        return monTrgtGenService.saveDto(dto);
    }
}
