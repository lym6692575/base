package com.example.demo.common.lee.util;

import java.math.BigDecimal;

/**
 * 类型转换工具类
 */
public class typeMapper {
    /**
     * 将任意对象安全转换为 BigDecimal 并按指定小数位进行四舍五入。
     * 支持的入参类型：
     * - BigDecimal：直接使用；
     * - Number（如 Integer、Long、Double 等）：通过 toString 构造 BigDecimal；
     * - CharSequence（如 String）：去除首尾空格后构造 BigDecimal；
     * - 其他或解析失败：返回按 scale 设定的小数位的 0。
     * 当入参为 null 时返回 0。
     *
     * @param value 任意值（通常为数据库读取到的单元格值）
     * @param scale 保留的小数位数（四舍五入）
     * @return 转换并按 scale 处理后的 BigDecimal，失败或空值返回 0（带 scale）
     */
    public static BigDecimal convertToBigDecimal(Object value, int scale) {
        return convertToBigDecimal(value, scale, BigDecimal.ZERO);
    }

    /**
     * 将任意对象安全转换为 BigDecimal，并按指定小数位进行四舍五入。
     * 当入参为空、不可解析或为不支持类型时，返回指定的默认值（按 scale 设定的小数位）。
     *
     * 支持的入参类型：
     * - BigDecimal：直接按 scale 处理；
     * - Number（如 Integer、Long、Double 等）：通过 toString 构造 BigDecimal；
     * - CharSequence（如 String）：去除首尾空格后构造 BigDecimal；
     * - 其他或解析失败：返回 defaultValue（若为 null 则使用 0），并按 scale 处理。
     *
     * @param value        任意值（通常为数据库读取到的单元格值）
     * @param scale        保留的小数位数（四舍五入）
     * @param defaultValue 默认值；为 null 时使用 0（均按 scale 处理）
     * @return 转换并按 scale 处理后的 BigDecimal；失败或空值返回默认值（带 scale）
     */
    public static BigDecimal convertToBigDecimal(Object value, int scale, BigDecimal defaultValue) {
        BigDecimal def = defaultValue != null ? defaultValue : BigDecimal.ZERO;
        if (value == null) {
            return def.setScale(scale, java.math.RoundingMode.HALF_UP);
        }
        BigDecimal bd;
        if (value instanceof BigDecimal) {
            bd = (BigDecimal) value;
        } else if (value instanceof Number) {
            bd = new BigDecimal(((Number) value).toString());
        } else if (value instanceof CharSequence) {
            String s = value.toString().trim();
            if (s.isEmpty()) return def.setScale(scale, java.math.RoundingMode.HALF_UP);
            try {
                bd = new BigDecimal(s);
            } catch (Exception ex) {
                return def.setScale(scale, java.math.RoundingMode.HALF_UP);
            }
        } else {
            return def.setScale(scale, java.math.RoundingMode.HALF_UP);
        }
        return bd.setScale(scale, java.math.RoundingMode.HALF_UP);
    }

    /**
     * 将字符串转换为 Double。如果输入为空或格式不正确，则返回 null。
     *
     * @param value 要转换的字符串
     * @return 转换后的 Double 或 null
     */
    public static Double convertToDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            System.out.println("无效的数字格式: " + value);
            throw new IllegalArgumentException("无效的数字格式: " + value, e);
        }
    }

    /**
     * 将字符串转换为 double。如果输入为空或格式不正确，则返回指定的默认值。
     *
     * @param value        要转换的字符串
     * @param defaultValue 转换失败时返回的默认值
     * @return 转换后的 double，如果转换失败则返回 defaultValue
     */
    public static double convertToDoubleOrDefault(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            // 转换失败时返回默认值
            return defaultValue;
        }
    }
}
