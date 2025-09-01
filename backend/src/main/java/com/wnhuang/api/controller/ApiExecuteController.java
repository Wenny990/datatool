package com.wnhuang.api.controller;

import com.wnhuang.api.domain.request.ApiCallRequest;
import com.wnhuang.api.domain.response.ApiCallResponse;
import com.wnhuang.api.service.ApiExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 统一API调用入口控制器
 * @author wnhuang
 * @date 2024/12/01
 */
@Slf4j
@RestController
@RequestMapping("/api/execute")
public class ApiExecuteController {

    @Autowired
    private ApiExecuteService apiExecuteService;

    /**
     * 统一API调用入口 - POST方式
     */
    @PostMapping("/call")
    public ApiCallResponse callApi(@RequestBody @Valid ApiCallRequest request, HttpServletRequest httpRequest) {
        String requestIp = getClientIpAddress(httpRequest);
        return apiExecuteService.executeApi(request, requestIp);
    }

    /**
     * 通过API编码直接调用 - GET方式
     */
    @GetMapping("/call/{apiCode}")
    public ApiCallResponse callApiByCode(
            @PathVariable String apiCode, 
            @RequestParam Map<String, Object> params,
            HttpServletRequest httpRequest) {
        
        ApiCallRequest request = new ApiCallRequest();
        request.setApiCode(apiCode);
        request.setParams(params);
        
        String requestIp = getClientIpAddress(httpRequest);

        return apiExecuteService.executeApi(request, requestIp);
    }

    /**
     * 通过API编码直接调用 - POST方式
     */
    @PostMapping("/call/{apiCode}")
    public ApiCallResponse callApiByCodePost(
            @PathVariable String apiCode, 
            @RequestBody(required = false) Map<String, Object> params,
            HttpServletRequest httpRequest) {
        
        ApiCallRequest request = new ApiCallRequest();
        request.setApiCode(apiCode);
        request.setParams(params);
        
        String requestIp = getClientIpAddress(httpRequest);

        return apiExecuteService.executeApi(request, requestIp);
    }

    /**
     * 仅返回数据，不包装ApiResult - GET方式
     */
    @GetMapping("/data/{apiCode}")
    public Object getApiData(
            @PathVariable String apiCode, 
            @RequestParam Map<String, Object> params,
            HttpServletRequest httpRequest) {
        
        ApiCallRequest request = new ApiCallRequest();
        request.setApiCode(apiCode);
        request.setParams(params);
        
        String requestIp = getClientIpAddress(httpRequest);
        ApiCallResponse response = apiExecuteService.executeApi(request, requestIp);
        
        if (response.getSuccess()) {
            return response.getData();
        } else {
            throw new RuntimeException(response.getErrorMessage());
        }
    }

    /**
     * 仅返回数据，不包装ApiResult - POST方式
     */
    @PostMapping("/data/{apiCode}")
    public Object getApiDataPost(
            @PathVariable String apiCode, 
            @RequestBody(required = false) Map<String, Object> params,
            HttpServletRequest httpRequest) {
        
        ApiCallRequest request = new ApiCallRequest();
        request.setApiCode(apiCode);
        request.setParams(params);
        
        String requestIp = getClientIpAddress(httpRequest);
        ApiCallResponse response = apiExecuteService.executeApi(request, requestIp);
        
        if (response.getSuccess()) {
            return response.getData();
        } else {
            throw new RuntimeException(response.getErrorMessage());
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}