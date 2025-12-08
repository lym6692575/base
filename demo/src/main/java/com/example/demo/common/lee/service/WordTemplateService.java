package com.example.demo.common.lee.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class WordTemplateService {

//    // 处理 .doc 文件
//    // HWPFDocument类没有与XWPFParagraph和XWPFRun类似的结构化方法来逐段处理文档
//    // 所以需要使用HWPFDocument和相关的类Range和Paragraph来处理
//    private void replacePlaceholdersInDoc(HWPFDocument document, Map<String, String> replacements) {
//        // Range对象表示Word文档中的一段文本范围
//        // document.getRange()方法返回整个文档的范围，包括所有段落、表格、标题等内容
//        Range range = document.getRange();
//        // 遍历替换映射中的每个键值对，并将文本块中的占位符替换为指定的值，entry.getKey() 是占位符（如${name}）
//        for (Map.Entry<String, String> entry : replacements.entrySet()) {
//            // 替换范围内的文本：在整个文本内找到所有匹配entry.getKey()的文本，并用entry.getValue()替换它们
//            range.replaceText(entry.getKey(), entry.getValue());
//        }
//    }

    // 处理 .docx 文件
    public void replacePlaceholdersInDocx(XWPFDocument document, Map<String, String> replacements) {
        // 对文档所有的段落做遍历
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            // 获取段落中的文本块
            // 每个段落 (XWPFParagraph) 由一个或多个文本块 (XWPFRun) 组成，文本块是段落中的最小文本单位，可以包含格式化信息（如字体、颜色）。
            List<XWPFRun> runs = paragraph.getRuns();
            // 若包含文本块，对其做处理
            if (runs != null) {
                for (XWPFRun run : runs) {
                    // 获取文本块中的文本内容，参数0表示获取文本块中的第一段文本（注：通常只有一段文本）
                    String text = run.getText(0);
                    if (text != null) {
                        // 创建StringBuilder处理字符串替换操作，提高性能，减少 String 对象的创建和销毁
                        StringBuilder textBuilder = new StringBuilder(text);
                        // 遍历替换映射中的每个键值对，并将文本块中的占位符替换为指定的值，entry.getKey() 是占位符（如${name}）
                        // 使用 StringBuilder 的 indexOf 方法找到占位符的位置，并使用 replace 方法进行替换
                        // 循环确保所有出现的占位符都被替换
                        for (Map.Entry<String, String> entry : replacements.entrySet()) {
                            int index = textBuilder.indexOf(entry.getKey());
                            while (index != -1) {
                                // replace 方法的参数分别是开始索引、结束索引和替换内容
                                textBuilder.replace(index, index + entry.getKey().length(), entry.getValue());
                                // 将其移动到刚替换的文本的末尾
                                index += entry.getValue().length();
                                // 从当前索引 index 开始继续查找下一个占位符，若找到下一个占位符，更新 index 为新位置
                                // 若找不到，index 将为 -1，循环结束
                                index = textBuilder.indexOf(entry.getKey(), index);
                            }
                        }
                        // 将替换后的文本重新设置到文本块中
                        run.setText(textBuilder.toString(), 0);
                    }
                }
            }
        }
    }
}