package com.wnhuang.datax.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wnhuang
 * @date 2024/11/1 01:55
 */
@Configuration
@Slf4j
public class ExecutorServiceConfig {

    @Value("${datax.maxThreadNum:10}")
    private int MAX_THREAD_NUM;

    @Bean("dataxExecutorService")
    public ExecutorService dataxExecutorService() {
        log.info("设置Datax并发执行最大数量为： {}", MAX_THREAD_NUM );
        return Executors.newFixedThreadPool(MAX_THREAD_NUM);
    }
}
