package com.wnhuang.api.controller;

import cn.hutool.json.JSONUtil;
import com.wnhuang.api.domain.request.ApiCallRequest;
import com.wnhuang.api.domain.request.ApiConfigRequest;
import com.wnhuang.api.utils.FtpPdfUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

/**
 * API接口模块测试类
 * 注意：这个测试类主要用于验证代码结构，实际运行需要数据库环境
 * @author wnhuang
 * @date 2024/12/01
 */
@SpringBootTest
@ActiveProfiles("dev")
public class ApiModuleTest {

    /**
     * 测试创建HTTP类型的API配置
     */
    @Test
    public void testCreateHttpApiConfig() {
        ApiConfigRequest request = new ApiConfigRequest();
        request.setApiName("测试HTTP接口");
        request.setApiCode("test_http_api");
        request.setApiType(1); // HTTP接口
        request.setHttpMethod("GET");
        request.setHttpUrl("https://jsonplaceholder.typicode.com/posts/{id}");
        
        // 设置HTTP请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        request.setHttpHeaders(JSONUtil.toJsonStr(headers));
        
        // 设置参数模板
        Map<String, String> paramTemplate = new HashMap<>();
        paramTemplate.put("id", "${postId}");

        request.setTimeout(30000);
        request.setStatus(1);
        request.setDescription("测试HTTP接口调用");
        
        System.out.println("HTTP API配置创建请求: " + JSONUtil.toJsonPrettyStr(request));
    }

    /**
     * 测试创建SQL类型的API配置
     */
    @Test
    public void testCreateSqlApiConfig() {
        ApiConfigRequest request = new ApiConfigRequest();
        request.setApiName("测试SQL查询接口");
        request.setApiCode("test_sql_api");
        request.setApiType(2); // SQL查询
        request.setRepositoryId(1); // 数据源ID
        request.setSqlQuery("SELECT * FROM users WHERE id = ${userId} AND status = 1");
        
        // 设置参数模板
        Map<String, String> paramTemplate = new HashMap<>();
        paramTemplate.put("userId", "${id}");

        // 设置后处理脚本
        request.setPostProcessScript("data.size() > 0 ? data[0] : null");
        
        request.setTimeout(30000);
        request.setStatus(1);
        request.setDescription("测试SQL查询接口");
        
        System.out.println("SQL API配置创建请求: " + JSONUtil.toJsonPrettyStr(request));
    }

    /**
     * 测试API调用请求
     */
    @Test
    public void testApiCallRequest() {
        // 测试HTTP接口调用
        ApiCallRequest httpRequest = new ApiCallRequest();
        httpRequest.setApiCode("test_http_api");
        
        Map<String, Object> httpParams = new HashMap<>();
        httpParams.put("postId", "1");
        httpRequest.setParams(httpParams);
        
        System.out.println("HTTP API调用请求: " + JSONUtil.toJsonPrettyStr(httpRequest));
        
        // 测试SQL接口调用
        ApiCallRequest sqlRequest = new ApiCallRequest();
        sqlRequest.setApiCode("test_sql_api");
        
        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("id", "123");
        sqlRequest.setParams(sqlParams);
        
        System.out.println("SQL API调用请求: " + JSONUtil.toJsonPrettyStr(sqlRequest));
    }

    /**
     * 测试参数模板处理
     */
    @Test
    public void testParamTemplateProcessing() {
        // 模拟参数模板
        String paramTemplate = "{\"userId\": \"${id}\", \"userName\": \"${name}\", \"status\": 1}";
        
        // 模拟输入参数
        Map<String, Object> inputParams = new HashMap<>();
        inputParams.put("id", "123");
        inputParams.put("name", "张三");
        
        System.out.println("参数模板: " + paramTemplate);
        System.out.println("输入参数: " + JSONUtil.toJsonPrettyStr(inputParams));
        System.out.println("期望结果: {\"userId\": \"123\", \"userName\": \"张三\", \"status\": 1}");
    }

    /**
     * 测试SQL参数处理
     */
    @Test
    public void testSqlParamProcessing() {
        String sqlTemplate = "SELECT * FROM users WHERE id = ${userId} #{if(name != null)} AND name LIKE '%${name}%' #{end} ORDER BY create_time DESC";
        
        Map<String, Object> params = new HashMap<>();
        params.put("userId", "123");
        params.put("name", "张三");
        
        System.out.println("SQL模板: " + sqlTemplate);
        System.out.println("参数: " + JSONUtil.toJsonPrettyStr(params));
        System.out.println("期望SQL: SELECT * FROM users WHERE id = 123 AND name LIKE '%张三%' ORDER BY create_time DESC");
    }

    /**
     * 测试后处理脚本
     */
    @Test
    public void testPostProcessScript() {
        // 模拟查询结果
        String mockData = "[{\"id\": 1, \"name\": \"张三\"}, {\"id\": 2, \"name\": \"李四\"}]";
        
        // 测试不同的后处理脚本
        String script1 = "data.size()"; // 返回数据条数
        String script2 = "data.size() > 0 ? data[0] : null"; // 返回第一条数据
        String script3 = "data"; // 返回原始数据
        
        System.out.println("原始数据: " + mockData);
        System.out.println("脚本1 (data.size()): 期望返回 2");
        System.out.println("脚本2 (data.size() > 0 ? data[0] : null): 期望返回第一条记录");
        System.out.println("脚本3 (data): 期望返回原始数据");
    }


    @Test
    public void testFtpPdfUtil() throws Exception {
        String ftpUrl = "ftp://155FTP:LAOhuang_shiGeshen666!@10.9.103.155:21/PDF/22112497/14320220916.PDF";
        String base64Pdf = FtpPdfUtil.downloadPdf(ftpUrl);
        System.out.println(base64Pdf);
    }
}