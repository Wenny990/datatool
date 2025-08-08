package com.wnhuang.db.aspect;

import com.wnhuang.common.service.RepositorySourceService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据源切换切面
 * 确保在Controller方法执行完成后切换回默认数据源
 */
@Aspect
@Component
@Slf4j
public class DataSourceAspect {

    @Autowired
    private RepositorySourceService repositorySourceService;

    /**
     * 定义切入点：所有Controller类的所有方法
     */
    @Pointcut("execution(* com.wnhuang..controller..*(..))")
    public void controllerMethods() {
        // 切入点定义
    }

    /**
     * 在Controller方法执行完成后，重置数据源为默认数据源
     */
    @After("controllerMethods()")
    public void resetDataSource() {
        try {
            repositorySourceService.resetRepository();
            log.debug("数据源已重置为默认数据源");
        } catch (Exception e) {
            log.warn("重置数据源失败: {}", e.getMessage(), e);
        }
    }
}
