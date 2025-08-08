package com.wnhuang.common.controller;


import com.wnhuang.common.domain.response.ApiResult;
import com.wnhuang.common.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("cache")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @GetMapping("/info")
    public ApiResult getCacheInfo() {
        return ApiResult.success(cacheService.getInfo());
    }

    @GetMapping
    public ApiResult getAllCacheKey() {
        return ApiResult.success(cacheService.getCacheNames("*"));
    }

    @GetMapping("/{cacheName}")
    public ApiResult getCacheKey(@PathVariable("cacheName") String cacheName) {
        return ApiResult.success(cacheService.getCacheNames(cacheName));
    }

    @GetMapping("/value/{cacheName}")
    public ApiResult getCacheValue(@PathVariable("cacheName") String cacheName) {
        return ApiResult.success(cacheService.getValue(cacheName));
    }

    @GetMapping("/clear/{cacheName}")
    public ApiResult clearCacheKey(@PathVariable("cacheName") String cacheName) {
        cacheService.clearCache(cacheName);
        return ApiResult.success(true);
    }

    @GetMapping("/clearAll")
    public ApiResult clearAll() {
        Long size = cacheService.clearCache("*");
        return ApiResult.success(size);
    }
}
