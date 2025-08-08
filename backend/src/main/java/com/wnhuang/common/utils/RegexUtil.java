package com.wnhuang.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    /**
     * 判断字符串是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        return str.matches("[0-9]*");
    }

    /**
     * 匹配字符串的内容
     * @param regex 表达式
     * @param content 内容
     * @return 返回匹配的结果
     */
    public static String match(String regex, String content) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
}
