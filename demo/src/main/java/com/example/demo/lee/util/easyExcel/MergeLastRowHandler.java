package com.example.demo.lee.util.easyExcel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

// 用 OnceWriteHandler 可以在整个 sheet 写完后做一次性处理
public class MergeLastRowHandler implements SheetWriteHandler {

    private final int dataSize;      // 主数据行数（你事先查询到）
    private final int headRowCount;  // 表头所占行数（含 relativeHeadRowIndex）
    private final int noteRowCount;  // 说明行数（如果只写了 1 行说明就填 1）

    public MergeLastRowHandler(int dataSize, int headRowCount, int noteRowCount) {
        this.dataSize = dataSize;
        this.headRowCount = headRowCount;
        this.noteRowCount = noteRowCount;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();

        // 计算“最后一行”在 Excel 里的行号（从 0 开始）
        // 比如 relativeHeadRowIndex(2) + 数据 dataSize + 说明行 noteRowCount 再减 1
        int lastRowIndex = headRowCount + dataSize + noteRowCount - 1;

        // 示例：合并最后一行的第 0~2 列
        sheet.addMergedRegion(new CellRangeAddress(lastRowIndex, lastRowIndex, 0, 3));
        // 示例：再合并最后一行的第 3~8 列
        sheet.addMergedRegion(new CellRangeAddress(lastRowIndex, lastRowIndex, 4, 6));
        sheet.addMergedRegion(new CellRangeAddress(lastRowIndex, lastRowIndex, 7, 10));
    }
}
