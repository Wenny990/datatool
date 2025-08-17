package com.wnhuang.db.aspect;

import com.wnhuang.common.service.RepositorySourceService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据源切换切面
 * 仅在Controller方法执行出现异常时重置数据源
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
     * 环绕通知：仅在方法执行出现异常时重置数据源
     */
    @Around("controllerMethods()")
    public Object resetDataSourceOnException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 正常执行方法
            return joinPoint.proceed();
        } catch (Exception ex) {
            // 仅在出现异常时重置数据源
            try {
                repositorySourceService.resetRepository();
                log.debug("数据源已重置为默认数据源（因异常）");
            } catch (Exception e) {
                log.warn("重置数据源失败: {}", e.getMessage(), e);
            }
            // 重新抛出原始异常
            throw ex;
        }
    }
}
