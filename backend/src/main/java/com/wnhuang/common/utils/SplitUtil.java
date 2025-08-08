package com.wnhuang.common.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 分隔工具类
 */
public class SplitUtil {

    /**
     * @param start 开始值
     * @param end   结束值
     * @param step  步长
     * @return 返回一个二维数组，数组元素为String类型
     * * 根据开始值，结束值和步长，切分成二维数组
     */
    public static List<List<String>> splitRange(Long start, Long end, Long step) {
        List<List<String>> result = new ArrayList<>();
        for (Long i = start; i <= end; i += step) {
            List<String> curr = new ArrayList<>();
            curr.add(Convert.toStr(i));
            curr.add(Convert.toStr(Math.min(i + step - 1, end)));
            result.add(curr);
        }
        return result;
    }

    /**
     * @param start 开始时间
     * @param end   结束时间
     * @param step  步长月数
     * @return 返回一个二维数组，数组元素为String类型
     * 根据开始时间，结束时间和步长月数，切分成二维数组
     */
    public static List<List<String>> splitDate(Date start, Date end, Integer step) {
        List<List<String>> result = new ArrayList<>();
        for (Date i = start; i.getTime() <= end.getTime(); i = DateUtil.offsetMonth(i, step)) {
            List<String> curr = new ArrayList<>();
            curr.add(DateUtil.format(i, DatePattern.NORM_DATE_PATTERN));
            DateTime offsetDay = DateUtil.offsetDay(DateUtil.offsetMonth(i, step), -1);
            curr.add(DateUtil.format(offsetDay.isBefore(end) ? offsetDay : end , DatePattern.NORM_DATE_PATTERN));
            result.add(curr);
        }
        return result;
    }
}
