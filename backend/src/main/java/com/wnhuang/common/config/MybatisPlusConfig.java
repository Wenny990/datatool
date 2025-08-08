package com.wnhuang.common.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wnhuang
 * @Package com.base.config
 * @date 2021/6/1 10:54
 */
@MapperScan("com.wnhuang.*.mapper")
@Configuration
public class MybatisPlusConfig {

    // 适配最新版SpringBoot的分页插件
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pageInterceptor = new PaginationInnerInterceptor();
        pageInterceptor.setMaxLimit(1000L);
        // 开启 count 的 join 优化,只针对部分 left join
        pageInterceptor.setOptimizeJoin(false);
        interceptor.addInnerInterceptor(pageInterceptor);
        return interceptor;
    }

    //处理特殊字符报错
    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "#|{}[]\\"));
        return factory;
    }


}
