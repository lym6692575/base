package com.example.demo.common.lee.util.easyExcel;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

public class ExcelHeadStyle {
    /**
     * 设置表头字体格式样式
     * @return
     */
    public static WriteCellStyle writExcelHeadStyle() {
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // 创建字体样式
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontName("宋体");  // 设置字体
        headWriteFont.setFontHeightInPoints((short) 12);  // 设置字号
        headWriteFont.setBold(true);  // 加粗
        headWriteCellStyle.setWriteFont(headWriteFont);

        // 设置背景颜色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        return headWriteCellStyle;
    }
}
