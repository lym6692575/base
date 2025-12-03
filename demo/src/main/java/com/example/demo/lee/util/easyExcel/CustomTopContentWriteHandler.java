package com.example.demo.lee.util.easyExcel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;
import java.util.Map;

/**
 * 动态顶部内容处理器，可传入多行多列的合并配置。
 */
public class CustomTopContentWriteHandler implements SheetWriteHandler {

    /**
     * 每一行存储若干个合并单元格块的信息
     */
    private final List<List<MergedCellInfo>> rows;

    /**
     * 列宽映射，key为列索引（从0开始），value为列宽（字符数）
     */
    private final Map<Integer, Integer> columnWidthMap;

    /**
     * 默认列宽（字符数）
     */
    private int defaultWidth;

    /**
     * 用于保存合并单元格信息的类
     */
    public static class MergedCellInfo {
        private String value;  // 显示的文本
        private int startCol;  // 起始列索引
        private int endCol;    // 结束列索引（>=startCol）

        public MergedCellInfo(String value, int startCol, int endCol) {
            this.value = value;
            this.startCol = startCol;
            this.endCol = endCol;
        }

        public String getValue() {
            return value;
        }

        public int getStartCol() {
            return startCol;
        }

        public int getEndCol() {
            return endCol;
        }
    }

    /**
     * 构造方法
     *
     * @param rows           要合并的单元格信息
     * @param columnWidthMap 列宽映射，key为列索引（从0开始），value为列宽（字符数）
     * @param defaultWidth   默认列宽（字符数）用于未在列宽映射中指定的列
     */
    public CustomTopContentWriteHandler(List<List<MergedCellInfo>> rows, Map<Integer, Integer> columnWidthMap, int defaultWidth) {
        this.rows = rows;
        this.columnWidthMap = columnWidthMap;
        this.defaultWidth = defaultWidth;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // do nothing
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        Workbook workbook = sheet.getWorkbook();

        // 创建不同的样式
        CellStyle firstRowStyle = createCellStyle(workbook, (short)16);
        CellStyle otherRowsStyle = createCellStyle(workbook, (short)12);

        // 设置列宽
        setColumnWidths(sheet);

        // 遍历行数据
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex);
            List<MergedCellInfo> cellInfos = rows.get(rowIndex);
            // 根据行索引选择样式
            CellStyle style = (rowIndex == 0) ? firstRowStyle : otherRowsStyle;

            for (MergedCellInfo cellInfo : cellInfos) {
                createAndMergeCell(row, cellInfo.getStartCol(), cellInfo.getEndCol(), cellInfo.getValue(), sheet, style);
            }
        }
    }
    /**
     * 设置列宽。为在 columnWidthMap 中指定的列设置指定宽度，为其他列设置默认宽度。
     *
     * @param sheet 当前工作表
     */
    private void setColumnWidths(Sheet sheet) {
        // 确定最大列索引
        int maxCol = getMaxColumnIndex(rows);

        // 设置每一列的宽度
        for (int col = 0; col <= maxCol; col++) {
            if (columnWidthMap != null && columnWidthMap.containsKey(col)) {
                sheet.setColumnWidth(col, columnWidthMap.get(col) * 256); // POI 的列宽单位是1/256个字符宽度
            } else {
                sheet.setColumnWidth(col, defaultWidth * 256); // 设置默认宽度
            }
        }
    }

    /**
     * 获取所有行中最大的列索引
     *
     * @param rows 所有合并单元格信息
     * @return 最大的列索引
     */
    private int getMaxColumnIndex(List<List<MergedCellInfo>> rows) {
        int maxCol = 0;
        if (rows != null) {
            for (List<MergedCellInfo> row : rows) {
                for (MergedCellInfo cellInfo : row) {
                    if (cellInfo.getEndCol() > maxCol) {
                        maxCol = cellInfo.getEndCol();
                    }
                }
            }
        }
        return maxCol;
    }

    /**
     * 工具方法：合并单元格并设置值和样式
     */
    private void createAndMergeCell(Row row, int startCol, int endCol, String value, Sheet sheet, CellStyle style) {
        Cell cell = row.createCell(startCol);
        cell.setCellValue(value);
        cell.setCellStyle(style);

        if (startCol != endCol) {
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), startCol, endCol));
            // 合并的区域内其他单元格也设置样式
            for (int i = startCol + 1; i <= endCol; i++) {
                Cell c = row.createCell(i);
                c.setCellStyle(style);
            }
        }
    }

    /**
     * 创建单元格样式，可根据需要动态调整
     *
     * @param workbook  工作簿对象
     * @param fontSize  字体大小
     * @return          创建好的单元格样式
     */
    private CellStyle createCellStyle(Workbook workbook, short fontSize) {
        CellStyle style = workbook.createCellStyle();
        // 对齐方式
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // 字体
        Font font = workbook.createFont();
        font.setFontHeightInPoints(fontSize);
        font.setBold(true);
        style.setFont(font);

        // 设置边框（可选）
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }
}
