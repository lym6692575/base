package com.example.demo.lee.util;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinyinInitialsUtils {

    /**
     * 根据传入的中文字符串获取拼音首字母
     *
     * @param chinese   待转换的中文字符串
     * @param uppercase 如果为 true，则返回大写；否则返回小写
     * @return 拼音首字母字符串
     */
    public static String getPinyinInitials(String chinese, boolean uppercase) {
        if (chinese == null || chinese.isEmpty()) {
            return "";
        }
        StringBuilder initials = new StringBuilder();
        for (char ch : chinese.toCharArray()) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch);
            if (pinyinArray != null && pinyinArray.length > 0) {
                initials.append(pinyinArray[0].charAt(0));
            } else {
                initials.append(ch); // 非汉字直接使用原字符
            }
        }
        return uppercase ? initials.toString().toUpperCase() : initials.toString().toLowerCase();
    }

    /**
     * 默认返回大写的拼音首字母
     *
     * @param chinese 待转换的中文字符串
     * @return 拼音首字母字符串（大写）
     */
    public static String getPinyinInitials(String chinese) {
        return getPinyinInitials(chinese, true);
    }
}
