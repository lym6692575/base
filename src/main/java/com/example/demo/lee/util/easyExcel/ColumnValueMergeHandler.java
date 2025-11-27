package com.example.demo.lee.util.easyExcel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * 根据数据行中指定列的相邻相同值进行合并的处理器。
 * 在 sheet 写入完成后一次性合并（通过预先传入的数据列表计算合并范围）。
 */
public class ColumnValueMergeHandler implements SheetWriteHandler {

    private final List<List<Object>> dataList;
    private final int headRowCount;
    private final int[] mergeCols;

    public ColumnValueMergeHandler(List<List<Object>> dataList, int headRowCount, int... mergeCols) {
        this.dataList = dataList;
        this.headRowCount = headRowCount;
        this.mergeCols = mergeCols;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        if (dataList == null || dataList.isEmpty() || mergeCols == null || mergeCols.length == 0) return;
        int startRow = headRowCount; // 数据起始行（从0开始）
        int endRow = headRowCount + dataList.size() - 1;
        for (int col : mergeCols) {
            int blockStart = startRow;
            Object prev = dataList.get(0).get(col);
            for (int i = 1; i < dataList.size(); i++) {
                Object cur = dataList.get(i).get(col);
                boolean equals = (prev == null && cur == null) || (prev != null && prev.equals(cur));
                if (!equals) {
                    int blockEnd = headRowCount + i - 1;
                    if (blockEnd > blockStart) {
                        sheet.addMergedRegion(new CellRangeAddress(blockStart, blockEnd, col, col));
                    }
                    blockStart = headRowCount + i;
                    prev = cur;
                }
            }
            // flush last block
            if (endRow > blockStart) {
                sheet.addMergedRegion(new CellRangeAddress(blockStart, endRow, col, col));
            }
        }
    }
}

