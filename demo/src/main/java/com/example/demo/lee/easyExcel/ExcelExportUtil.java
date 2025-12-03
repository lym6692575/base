package com.example.demo.lee.easyExcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.WriteSheet;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

@Service
public class ExcelExportUtil {

    public <T> void exportExcelCallBack(HttpServletResponse response, String fileName,
                                Consumer<ServletOutputStream> writeCallback) {
        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName + ".xlsx");

            writeCallback.accept(response.getOutputStream());
        } catch (Exception e) {
            String type = e.getClass().getSimpleName();
            String msg = e.getMessage();
            handleException(response, "导出Excel失败：" + (msg == null ? type : (type + ": " + msg)));
        }
    }

    /**
     * 异常处理：输出错误信息到响应
     *
     * @param response 响应对象
     * @param message  错误信息
     */
    private void handleException(HttpServletResponse response, String message) {
        try {
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\":\"" + message.replace("\"", "\\\"") + "\"}");
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
        }
    }

    /**
     * 导出数据到Excel
     *
     * @param response    HttpServletResponse 对象
     * @param data        要导出的数据列表
     * @param fileName    导出的文件名（不含扩展名）
     * @param sheetName   工作表名称
     * @param clazz       数据类型对应的Class对象
     */
    public <T> void exportExcel(HttpServletResponse response, List<T> data, String fileName, String sheetName, Class<T> clazz) {
        try {
            // 设置响应头
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName + ".xlsx");

            // 创建写入器
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

            // 写入数据
            EasyExcel.write(response.getOutputStream(), clazz)
                    .sheet(sheetName)
                    .doWrite(data);
        } catch (IOException e) {
            try {
                response.reset();
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"message\":\"导出Excel失败\"}");
            } catch (IOException ioException) {
            }
        } catch (Exception e) {
            try {
                response.reset();
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"message\":\"导出Excel失败\"}");
            } catch (IOException ioException) {
            }
        }
    }

    /**
     * 导出动态表头和数据
     *
     * @param response  HttpServletResponse
     * @param headList  List<List<String>> 每个 List<String> 就是一列的表头（单层时直接 singletonList）
     * @param dataList  List<List<Object>> 按 headList 顺序把每行的数据提取成 List<Object>
     * @param fileName  文件名
     * @param sheetName 工作表名
     */
    public void exportExcel(HttpServletResponse response,
                            List<List<String>> headList,
                            List<List<Object>> dataList,
                            String fileName,
                            String sheetName) {
        try {
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            EasyExcel.write(response.getOutputStream())
                    .head(headList)
                    .sheet(sheetName)
                    .doWrite(dataList);
        } catch (IOException e) {
            // 错误处理同你现有方法
        }
    }
}
