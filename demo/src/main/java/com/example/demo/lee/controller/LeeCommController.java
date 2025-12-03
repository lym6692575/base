package com.example.demo.lee.controller;

import com.example.demo.lee.service.BaseSqlMapperService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * controller
 */

@RestController
@RequestMapping("comm")
public class LeeCommController {
    private final BaseSqlMapperService baseSqlMapperService;

    public LeeCommController(BaseSqlMapperService baseSqlMapperService) {
        this.baseSqlMapperService = baseSqlMapperService;
    }

    @GetMapping("/dropdown/{type}")
    public List<Map<String, Object>> findDropDtoPriSource(@PathVariable String type) {
        return baseSqlMapperService.getDropdownData(type, false);
    }

    @GetMapping("/dropdown/flame/{type}")
    public List<Map<String, Object>> findDropDtoSecSource(@PathVariable String type) {
        return baseSqlMapperService.getDropdownData(type, true);
    }

    /**
     * 带参数的下拉框数据查询
     * <p>
     * 客户端应按照 `base_sql_mapper` 中 `conditions` 的占位符顺序传递参数。
     * 例如，若 `conditions` 为 "id = ? AND type = ?", 则需要传递 `id` 和 `type` 两个参数。
     *
     * @param type   映射键，例如 "LLJ"
     * @param params 查询参数，按顺序绑定到 SQL 中的占位符
     * @return 下拉框数据列表
     */
    @GetMapping("/dropdown/{type}/with-params")
    public List<Map<String, Object>> getDropdownDataWithParams(
            @PathVariable("type") String type,
            @RequestParam(value = "params", required = false) List<String> params) {

        List<Object> convertedParams = convertParams(params);

        return baseSqlMapperService.getDropdownDataWithParams(type, false, convertedParams.toArray());
    }

    /**
     * 将字符串参数转换为适当的类型。
     * 您可以根据实际情况扩展此方法，以支持更多的数据类型。
     *
     * @param params 字符串形式的参数列表
     * @return 转换后的参数列表
     */
    private List<Object> convertParams(List<String> params) {
        List<Object> converted = new java.util.ArrayList<>();
        for (String param : params) {
            if (param.startsWith("[") && param.endsWith("]")) {
                // 如果是类似 [1, 2, 3] 的数组格式
                String arrayContent = param.substring(1, param.length() - 1); // 去掉 [] 部分
                String[] elements = arrayContent.split(",\\s*"); // 按照逗号分割
                converted.add(elements); // 将元素作为数组存储
            } else {
                try {
                    converted.add(Integer.parseInt(param));
                } catch (NumberFormatException e) {
                    converted.add(param);
                }
            }
        }
        return converted;
    }
}
