package com.wnhuang.api.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wnhuang.common.aviator.AviatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数处理工具类
 * @author wnhuang
 * @date 2024/12/01
 */
@Slf4j
@Component
public class ParamProcessUtil {

    /**
     * 参数占位符模式，支持 ${paramName} 格式
     */
    private static final Pattern PARAM_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    /**
     * 处理参数模板，将占位符替换为实际参数值
     *
     * @param template 参数模板（JSON格式）
     * @param params   实际参数
     * @return 处理后的参数Map
     */
    public Map<String, Object> processParamTemplate(String template, Map<String, Object> params) {
        if (StrUtil.isBlank(template)) {
            return params != null ? params : new HashMap<>();
        }

        try {
            // 解析模板
            JSONObject templateObj = JSONUtil.parseObj(template);
            Map<String, Object> processedParams = new HashMap<>();

            // 处理每个模板参数
            for (String key : templateObj.keySet()) {
                Object templateValue = templateObj.get(key);
                if (templateValue instanceof String) {
                    String strValue = (String) templateValue;
                    // 替换占位符
                    String processedValue = replacePlaceholders(strValue, params);
                    processedParams.put(key, processedValue);
                } else {
                    processedParams.put(key, templateValue);
                }
            }

            // 合并用户传入的参数（用户参数优先级更高）
            if (params != null) {
                processedParams.putAll(params);
            }

            log.info("参数模板处理完成，原模板: {}, 输入参数: {}, 处理结果: {}", template, params, processedParams);
            return processedParams;

        } catch (Exception e) {
            log.error("参数模板处理失败，模板: {}, 参数: {}", template, params, e);
            throw new RuntimeException("参数模板处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 替换字符串中的占位符
     *
     * @param template 包含占位符的字符串
     * @param params   参数Map
     * @return 替换后的字符串
     */
    private String replacePlaceholders(String template, Map<String, Object> params) {
        if (StrUtil.isBlank(template) || params == null || params.isEmpty()) {
            return template;
        }

        Matcher matcher = PARAM_PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String paramName = matcher.group(1);
            Object paramValue = params.get(paramName);
            String replacement = paramValue != null ? paramValue.toString() : "${" + paramName + "}";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * 处理SQL查询参数，将占位符替换为实际值
     *
     * @param sql    SQL语句
     * @param params 参数Map
     * @return 处理后的SQL语句
     */
    public String processSqlParams(String sql, Map<String, Object> params) {
        if (StrUtil.isBlank(sql)) {
            return sql;
        }

        try {
            // 使用Aviator表达式引擎处理SQL参数
            if (params != null && !params.isEmpty()) {
                return AviatorUtils.compileSql(sql, JSONUtil.parseObj(params));
            }
            return sql;
        } catch (Exception e) {
            log.error("SQL参数处理失败，SQL: {}, 参数: {}", sql, params, e);
            throw new RuntimeException("SQL参数处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行后处理脚本
     *
     * @param script 后处理脚本
     * @param data   原始数据
     * @return 处理后的数据
     */
    public Object executePostProcessScript(String script, Object data) {
        if (StrUtil.isBlank(script)) {
            return data;
        }

        try {
            // 将数据放入上下文
            Map<String, Object> context = new HashMap<>();
            context.put("data", data);
            context.put("result", data);

            // 使用Aviator执行脚本
            String result = AviatorUtils.compileSql(script, context);
            
            log.info("后处理脚本执行完成，脚本: {}, 原始数据: {}, 处理结果: {}", script, data, result);
            return result;

        } catch (Exception e) {
            log.error("后处理脚本执行失败，脚本: {}, 数据: {}", script, data, e);
            throw new RuntimeException("后处理脚本执行失败: " + e.getMessage(), e);
        }
    }
}