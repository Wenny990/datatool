package com.wnhuang.common.aviator;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Expression;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author wnhuang
 * @Package com.base.aviator
 * @date 2022/3/17 18:09
 */
public class AviatorUtils {

    private static final AviatorEvaluatorInstance aviatorInstance = AviatorEvaluator.newInstance();

    static {
        try {
            aviatorInstance.addStaticFunctions("str", StrUtil.class);
            aviatorInstance.addInstanceFunctions("s", String.class);
            aviatorInstance.addStaticFunctions("date", DateUtil.class);
            aviatorInstance.addStaticFunctions("random", RandomUtil.class);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    public static AviatorEvaluatorInstance getInstance(){
        return aviatorInstance;
    }

    public static String compileSql(String sql, Map<String, Object> param) {
        List<String> allMatchString = getAllMatchString(sql);
        for (String t : allMatchString) {
            String substring = t.substring(2, t.length() - 1);
            Object execute = aviatorInstance.execute(substring, param);
            sql = sql.replace(t, Convert.toStr(execute, ""));
        }
        return sql;
    }

    public static Object compileString(String key, String template, Map<String, Object> param) {
        Expression compile = aviatorInstance.compile(template, true);
        return compile.execute(param);
    }

    public static void removeAviatorCacheByKey(String key){
        aviatorInstance.invalidateCacheByKey(key);
    }

    public static List<String> compileVariableNamesBySql(String sql) {
        List<String> variableNames = new ArrayList<>();
        List<String> allMatchString = getAllMatchString(sql);
        allMatchString.forEach(t -> {
            String substring = t.substring(2, t.length() - 1);
            aviatorInstance.validate(substring);
            Expression compiledExp = aviatorInstance.compile(substring);
            variableNames.addAll(compiledExp.getVariableNames());
        });
        return variableNames.stream().distinct().collect(Collectors.toList());
    }

    public static List<String> getAllMatchString(String sql) {
        Assert.hasText(sql, "传入的SQL不能为空！");
//        Assert.isTrue(isSelect(sql),"传入的语句非法！");
        //匹配#{*}格式的占位参数
        Pattern pattern = Pattern.compile("#\\{(?:[^{}#]+|\\{(?:[^{}]+|\\{[^{}]*\\})*})+\\}");
        Matcher matcher = pattern.matcher(sql);
        List<String> matchList = new ArrayList<>();
        while (matcher.find()) {
            matchList.add(matcher.group());
        }
        return matchList;
    }

    public static Boolean validate(String expression){
        aviatorInstance.validate(expression);
        return true;
    }

//    public static boolean isSelect(String sql) {
//        return sql.matches("^(?i)(\\s*)(select)(\\s+)(((?!(insert|delete|update|truncate)).)+)$");
//    }
}
