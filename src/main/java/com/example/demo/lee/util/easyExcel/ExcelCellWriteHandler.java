package com.example.demo.lee.util.easyExcel;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public class ExcelCellWriteHandler extends AbstractColumnWidthStyleStrategy {
    private static final int MAX_COLUMN_WIDTH = 255;
    private static final int MIN_COLUMN_WIDTH = 8;

    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        int columnIndex = cell.getColumnIndex();
        Sheet sheet = writeSheetHolder.getSheet();

        // 如果是表头，直接设置宽度
        if (isHead) {
            String text = cell.getStringCellValue();
            int width = text.length() * 2 + 2;
            width = Math.min(width, MAX_COLUMN_WIDTH);
            width = Math.max(width, MIN_COLUMN_WIDTH);
            sheet.setColumnWidth(columnIndex, width * 256);
        }
        // 如果是内容单元格，计算并比较是否需要更新宽度
        else {
            // 获取当前列宽
            int currentWidth = sheet.getColumnWidth(columnIndex) / 256;

            // 计算内容所需宽度
            int contentWidth = calculateCellWidth(cell);
            contentWidth = Math.min(contentWidth, MAX_COLUMN_WIDTH);
            contentWidth = Math.max(contentWidth, MIN_COLUMN_WIDTH);

            // 如果内容比当前列宽更宽，则更新列宽
            if (contentWidth > currentWidth) {
                sheet.setColumnWidth(columnIndex, contentWidth * 256);
            }
        }
    }

    /**
     * 根据单元格内容计算合适的宽度
     * @param cell
     * @return
     */
    private int calculateCellWidth(Cell cell) {
        String text;
        switch (cell.getCellType()) {
            case STRING:
                text = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    text = cell.getDateCellValue().toString();
                } else {
                    text = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                text = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                text = cell.getCellFormula();
                break;
            default:
                text = "";
        }

        // 简单计算：中文字符算2个宽度，其他算1个
        int width = 0;
        for (char c : text.toCharArray()) {
            //0x4E00 到 0x9FA5 是 Unicode 中 CJK 统一表意文字的范围（即中文字符）
            width += (c >= 0x4E00 && c <= 0x9FA5) ? 2 : 1;
        }

        return width + 2; // 加2作为边距

    }
}
