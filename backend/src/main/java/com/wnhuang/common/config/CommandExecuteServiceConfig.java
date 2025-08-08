package com.wnhuang.common.config;

import com.wnhuang.common.service.CommandExecuteService;
import com.wnhuang.common.service.SessionPool;
import com.wnhuang.common.service.impl.ApacheCommandExecuteServiceImpl;
import com.wnhuang.common.service.impl.SshCommandExecuteServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wnhuang
 * @date 2025/1/13 22:52
 */
@Configuration
public class CommandExecuteServiceConfig {

    @Bean
    @ConditionalOnProperty(name = "command.execute.service", havingValue = "apache")
    public CommandExecuteService defaultCommandExecuteService() {
        return new ApacheCommandExecuteServiceImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "command.execute.service", havingValue = "jsch")
    public CommandExecuteService apacheCommandExecuteService(SessionPool sessionPool) {
        return new SshCommandExecuteServiceImpl(sessionPool);
    }
}
