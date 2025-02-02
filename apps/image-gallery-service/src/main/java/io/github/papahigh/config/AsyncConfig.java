package io.github.papahigh.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@EnableAsync
@Configuration
@EnableConfigurationProperties({AsyncConfig.IOTaskExecutorProperties.class})
public class AsyncConfig {

    @Bean(name = "io")
    public Executor ioTaskExecutor(IOTaskExecutorProperties properties) {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setThreadNamePrefix(properties.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }

    @Data
    @ConfigurationProperties(prefix = "async.io")
    public static class IOTaskExecutorProperties {
        private int corePoolSize = 2;
        private int maxPoolSize = Runtime.getRuntime().availableProcessors();
        private int queueCapacity = 1000;
        private String threadNamePrefix = "async-io-";
    }
}
