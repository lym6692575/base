package com.example.demo.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 小李工具类
 */


public class LeeUtils {
    /**
     * 根据中文字符获取拼音首字母
     *
     * @param chinese 中文字符
     * @return 拼音首字母，如果转换失败则返回空字符串
     */
    public static String getFirstLetterFromPinyin(String chinese) {
        StringBuilder pinyinBuilder = new StringBuilder();

        for (char c : chinese.toCharArray()) {
            if (isChineseCharacter(c)) {
                try {
                    String firstLetter = getChineseFirstLetter(String.valueOf(c));
                    pinyinBuilder.append(firstLetter);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    // 处理拼音转换异常
                    System.err.println("拼音转换异常：" + e.getMessage());
                    return "";
                }
            } else {
                pinyinBuilder.append(c);
            }
        }

        return pinyinBuilder.toString();
    }

    /**
     * 判断字符是否为中文字符
     *
     * @param c 字符
     * @return 是否为中文字符
     */
    private static boolean isChineseCharacter(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;
    }

    /**
     * 将中文字符转换为拼音首字母
     *
     * @param chinese 中文字符
     * @return 拼音首字母
     * @throws BadHanyuPinyinOutputFormatCombination 拼音输出格式异常
     */
    private static String getChineseFirstLetter(String chinese) throws BadHanyuPinyinOutputFormatCombination {
        String pinyin = PinyinHelper.toHanyuPinyinStringArray(chinese.charAt(0))[0];
        return String.valueOf(pinyin.charAt(0)).toUpperCase();
    }
}
