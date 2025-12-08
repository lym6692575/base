package com.example.demo.common.lee.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class TimeUtil {
    public static String normalize(String raw) {
        // 1. 把 “/” 换成 “-”
        raw = raw.replace("/", "-");
        // 2. 解析器，允许有或无秒
        DateTimeFormatter parser = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-M-d H:mm")
                .optionalStart().appendPattern(":ss").optionalEnd()
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();
        LocalDateTime dt = LocalDateTime.parse(raw, parser);
        // 3. 格式化到 yyyy-MM-dd HH:mm:ss
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
