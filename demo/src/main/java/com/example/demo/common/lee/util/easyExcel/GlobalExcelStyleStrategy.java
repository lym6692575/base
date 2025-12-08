package com.example.demo.common.lee.util.easyExcel;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * 全局样式策略类，为表头和内容统一设置样式。
 */
public class GlobalExcelStyleStrategy extends HorizontalCellStyleStrategy {

    public GlobalExcelStyleStrategy() {
        super(headStyle(), contentStyle());
    }

    /**
     * 表头样式
     */
    private static WriteCellStyle headStyle() {
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 表头不要背景颜色，因此不设置 fillForegroundColor

        // 设置表头字体
        WriteFont headWriteFont = new WriteFont();
//        headWriteFont.setBold(true);
        headWriteFont.setFontHeightInPoints((short) 12);
        headWriteCellStyle.setWriteFont(headWriteFont);

        return headWriteCellStyle;
    }

    /**
     * 内容样式（加上框线）
     */
    private static WriteCellStyle contentStyle() {
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 设置内容字体
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontHeightInPoints((short) 12);
        contentWriteCellStyle.setWriteFont(contentWriteFont);

        // 设置边框样式和颜色
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);

        // 可选：设置边框颜色为更明显的颜色，如黑色
        contentWriteCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        contentWriteCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        contentWriteCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        contentWriteCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

        return contentWriteCellStyle;
    }
}
