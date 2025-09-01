package com.wnhuang.api.utils;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.wnhuang.api.domain.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP客户端工具类
 * @author wnhuang
 */
@Slf4j
@Component
public class HttpClientUtil {

    /**
     * 执行GET请求
     *
     * @param url     请求URL
     * @param headers 请求头
     * @param params  请求参数
     * @param timeout 超时时间（毫秒）
     * @return 响应结果
     */
    public String doGet(String url, Map<String, String> headers, Map<String, Object> params, int timeout) {
        try {
            log.info("执行GET请求，URL: {}, 参数: {}", url, params);

            HttpRequest request = HttpUtil.createGet(url);

            // 设置超时时间
            request.timeout(timeout);

            // 设置请求头
            if (headers != null && !headers.isEmpty()) {
                request.headerMap(headers, true);
            }

            // 设置请求参数
            if (params != null && !params.isEmpty()) {
                request.form(params);
            }

            try (HttpResponse response = request.execute()) {
                String result = response.body();

                log.info("GET请求响应状态: {}, 响应内容: {}", response.getStatus(), result);

                if (response.isOk()) {
                    return result;
                } else {
                    throw new RuntimeException("HTTP请求失败，状态码: " + response.getStatus() + ", 响应: " + result);
                }
            }

        } catch (Exception e) {
            log.error("GET请求执行失败，URL: {}", url, e);
            throw new RuntimeException("GET请求执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行POST请求
     *
     * @param url     请求URL
     * @param headers 请求头
     * @param params  请求参数
     * @param timeout 超时时间（毫秒）
     * @return 响应结果
     */
    public String doPost(String url, Map<String, String> headers, Map<String, Object> params, int timeout) {
        try {
            log.info("执行POST请求，URL: {}, 参数: {}", url, params);

            HttpRequest request = HttpUtil.createPost(url);

            // 设置超时时间
            request.timeout(timeout);

            // 设置请求头
            if (headers != null && !headers.isEmpty()) {
                request.headerMap(headers, true);
            }

            // 判断是否为JSON请求
            String contentType = headers != null ? headers.get("Content-Type") : null;
            if (StrUtil.isNotBlank(contentType) && contentType.contains("application/json")) {
                // JSON格式请求
                if (params != null && !params.isEmpty()) {
                    request.body(JSONUtil.toJsonStr(params));
                }
            } else {
                // 表单格式请求
                if (params != null && !params.isEmpty()) {
                    request.form(params);
                }
            }

            try (HttpResponse response = request.execute()) {
                String result = response.body();

                log.info("POST请求响应状态: {}, 响应内容: {}", response.getStatus(), result);

                if (response.isOk()) {
                    return result;
                } else {
                    throw new RuntimeException("HTTP请求失败，状态码: " + response.getStatus() + ", 响应: " + result);
                }
            }

        } catch (Exception e) {
            log.error("POST请求执行失败，URL: {}", url, e);
            throw new RuntimeException("POST请求执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行PUT请求
     *
     * @param url     请求URL
     * @param headers 请求头
     * @param params  请求参数
     * @param timeout 超时时间（毫秒）
     * @return 响应结果
     */
    public String doPut(String url, Map<String, String> headers, Map<String, Object> params, int timeout) {
        try {
            log.info("执行PUT请求，URL: {}, 参数: {}", url, params);

            HttpRequest request = HttpUtil.createRequest(Method.PUT, url);

            // 设置超时时间
            request.timeout(timeout);

            // 设置请求头
            if (headers != null && !headers.isEmpty()) {
                request.headerMap(headers, true);
            }

            // 判断是否为JSON请求
            String contentType = headers != null ? headers.get("Content-Type") : null;
            if (StrUtil.isNotBlank(contentType) && contentType.contains("application/json")) {
                // JSON格式请求
                if (params != null && !params.isEmpty()) {
                    request.body(JSONUtil.toJsonStr(params));
                }
            } else {
                // 表单格式请求
                if (params != null && !params.isEmpty()) {
                    request.form(params);
                }
            }

            try (HttpResponse response = request.execute()) {
                String result = response.body();

                log.info("PUT请求响应状态: {}, 响应内容: {}", response.getStatus(), result);

                if (response.isOk()) {
                    return result;
                } else {
                    throw new RuntimeException("HTTP请求失败，状态码: " + response.getStatus() + ", 响应: " + result);
                }
            }

        } catch (Exception e) {
            log.error("PUT请求执行失败，URL: {}", url, e);
            throw new RuntimeException("PUT请求执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行DELETE请求
     *
     * @param url     请求URL
     * @param headers 请求头
     * @param params  请求参数
     * @param timeout 超时时间（毫秒）
     * @return 响应结果
     */
    public String doDelete(String url, Map<String, String> headers, Map<String, Object> params, int timeout) {
        try {
            log.info("执行DELETE请求，URL: {}, 参数: {}", url, params);

            HttpRequest request = HttpUtil.createRequest(Method.DELETE, url);

            // 设置超时时间
            request.timeout(timeout);

            // 设置请求头
            if (headers != null && !headers.isEmpty()) {
                request.headerMap(headers, true);
            }

            // 设置请求参数
            if (params != null && !params.isEmpty()) {
                request.form(params);
            }

            try (HttpResponse response = request.execute()) {
                String result = response.body();

                log.info("DELETE请求响应状态: {}, 响应内容: {}", response.getStatus(), result);

                if (response.isOk()) {
                    return result;
                } else {
                    throw new RuntimeException("HTTP请求失败，状态码: " + response.getStatus() + ", 响应: " + result);
                }
            }

        } catch (Exception e) {
            log.error("DELETE请求执行失败，URL: {}", url, e);
            throw new RuntimeException("DELETE请求执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据HTTP配置执行请求
     *
     * @param config HTTP请求配置
     * @param params 动态参数
     * @return 响应结果
     */
    public String executeHttpRequest(HttpRequestConfig config, Map<String, Object> params) {
        try {
            log.info("执行HTTP请求，配置: {}, 参数: {}", config, params);

            String method = config.getMethod().toUpperCase();
            String url = processUrlWithParams(config.getUrl(), params);

            HttpRequest request = createHttpRequest(method, url);

            // 设置超时时间
            if (config.getTimeout() != null) {
                request.timeout(config.getTimeout());
            }

            // 设置请求头
            setHeaders(request, config.getHeaders(), params);

            // 设置查询参数
            setQueryParams(request, config.getQueryParams(), params);

            // 设置请求体
            setRequestBody(request, config.getBody(), params);

            try (HttpResponse response = request.execute()) {
                String result = response.body();

                log.info("HTTP请求响应状态: {}, 响应内容: {}", response.getStatus(), result);

                if (response.isOk()) {
                    return result;
                } else {
                    throw new RuntimeException("HTTP请求失败，状态码: " + response.getStatus() + ", 响应: " + result);
                }
            }

        } catch (Exception e) {
            log.error("HTTP请求执行失败，配置: {}", config, e);
            throw new RuntimeException("HTTP请求执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建HTTP请求对象
     */
    private HttpRequest createHttpRequest(String method, String url) {
        switch (method) {
            case "GET":
                return HttpUtil.createGet(url);
            case "POST":
                return HttpUtil.createPost(url);
            case "PUT":
                return HttpUtil.createRequest(Method.PUT, url);
            case "DELETE":
                return HttpUtil.createRequest(Method.DELETE, url);
            case "PATCH":
                return HttpUtil.createRequest(Method.PATCH, url);
            default:
                throw new RuntimeException("不支持的HTTP方法: " + method);
        }
    }

    /**
     * 处理URL中的参数占位符
     */
    private String processUrlWithParams(String url, Map<String, Object> params) {
        if (StrUtil.isBlank(url) || params == null || params.isEmpty()) {
            return url;
        }

        String processedUrl = url;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            if (processedUrl.contains(placeholder)) {
                processedUrl = processedUrl.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        return processedUrl;
    }

    /**
     * 设置请求头
     */
    private void setHeaders(HttpRequest request, List<HttpHeader> headers, Map<String, Object> params) {
        if (headers == null || headers.isEmpty()) {
            return;
        }

        Map<String, String> headerMap = new HashMap<>();
        for (HttpHeader header : headers) {
            if (header.getEnabled() != null && header.getEnabled()) {
                String value = processParamPlaceholders(header.getValue(), params);
                headerMap.put(header.getKey(), value);
            }
        }

        if (!headerMap.isEmpty()) {
            request.headerMap(headerMap, true);
        }
    }

    /**
     * 设置查询参数
     */
    private void setQueryParams(HttpRequest request, List<HttpQueryParam> queryParams, Map<String, Object> params) {
        if (queryParams == null || queryParams.isEmpty()) {
            return;
        }

        Map<String, Object> queryMap = new HashMap<>();
        for (HttpQueryParam queryParam : queryParams) {
            if (queryParam.getEnabled() != null && queryParam.getEnabled()) {
                String value = processParamPlaceholders(queryParam.getValue(), params);
                queryMap.put(queryParam.getKey(), value);
            }
        }

        if (!queryMap.isEmpty()) {
            if(request.getMethod().equals(Method.GET)){
                request.form(queryMap);
            }else{
                UrlBuilder builder = UrlBuilder.of(request.getUrl(), Charset.defaultCharset());
                queryMap.forEach(builder::addQuery);
                request.setUrl(builder.build());
            }

        }
    }

    /**
     * 设置请求体
     */
    private void setRequestBody(HttpRequest request, HttpBody body, Map<String, Object> params) {
        if (body == null || StrUtil.isBlank(body.getType())) {
            return;
        }

        switch (body.getType().toLowerCase()) {
            case "none":
                // 无请求体
                break;
            case "form-data":
                setFormDataBody(request, body.getFormData(), params);
                break;
            case "x-www-form-urlencoded":
                setUrlEncodedBody(request, body.getUrlEncoded(), params);
                break;
            case "raw":
                setRawBody(request, body.getRaw(), params);
                break;
            case "binary":
                setBinaryBody(request, body.getBinary());
                break;
            default:
                log.warn("不支持的请求体类型: {}", body.getType());
        }
    }

    /**
     * 设置Form-Data请求体
     */
    private void setFormDataBody(HttpRequest request, List<HttpBody.FormDataParam> formData, Map<String, Object> params) {
        if (formData == null || formData.isEmpty()) {
            return;
        }

        Map<String, Object> formMap = new HashMap<>();
        for (HttpBody.FormDataParam param : formData) {
            if (param.getEnabled() != null && param.getEnabled()) {
                String value = processParamPlaceholders(param.getValue(), params);
                formMap.put(param.getKey(), value);
            }
        }

        if (!formMap.isEmpty()) {
            request.form(formMap);
        }
    }

    /**
     * 设置URL编码请求体
     */
    private void setUrlEncodedBody(HttpRequest request, List<HttpBody.UrlEncodedParam> urlEncoded, Map<String, Object> params) {
        if (urlEncoded == null || urlEncoded.isEmpty()) {
            return;
        }

        Map<String, Object> formMap = new HashMap<>();
        for (HttpBody.UrlEncodedParam param : urlEncoded) {
            if (param.getEnabled() != null && param.getEnabled()) {
                String value = processParamPlaceholders(param.getValue(), params);
                formMap.put(param.getKey(), value);
            }
        }

        if (!formMap.isEmpty()) {
            request.form(formMap);
        }
    }

    /**
     * 设置Raw请求体
     */
    private void setRawBody(HttpRequest request, HttpBody.RawData raw, Map<String, Object> params) {
        if (raw == null || StrUtil.isBlank(raw.getContent())) {
            return;
        }

        String content = processParamPlaceholders(raw.getContent(), params);
        request.body(content);

        // 设置Content-Type
        if (StrUtil.isNotBlank(raw.getContentType())) {
            String contentType = getContentTypeByFormat(raw.getContentType());
            request.header("Content-Type", contentType);
        }
    }

    /**
     * 设置Binary请求体
     */
    private void setBinaryBody(HttpRequest request, HttpBody.BinaryData binary) {
        if (binary == null || StrUtil.isBlank(binary.getFileData())) {
            return;
        }

        // 这里简化处理，实际项目中可能需要处理文件上传
        request.body(binary.getFileData());

        if (StrUtil.isNotBlank(binary.getContentType())) {
            request.header("Content-Type", binary.getContentType());
        }
    }

    /**
     * 根据格式获取Content-Type
     */
    private String getContentTypeByFormat(String format) {
        switch (format.toUpperCase()) {
            case "JSON":
                return "application/json";
            case "XML":
                return "application/xml";
            case "HTML":
                return "text/html";
            case "JAVASCRIPT":
                return "application/javascript";
            case "TEXT":
            default:
                return "text/plain";
        }
    }

    /**
     * 处理参数占位符
     */
    private String processParamPlaceholders(String template, Map<String, Object> params) {
        if (StrUtil.isBlank(template) || params == null || params.isEmpty()) {
            return template;
        }

        String processed = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            if (processed.contains(placeholder)) {
                processed = processed.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        return processed;
    }
}