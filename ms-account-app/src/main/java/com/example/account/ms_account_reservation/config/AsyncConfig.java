package com.example.account.ms_account_reservation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Value("${app.async.core-pool-size}")
    private int corePoolSize;

    @Value("${app.async.max-pool-size}")
    private int maxPoolSize;

    @Value("${app.async.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "currencyExecutor")
    public ThreadPoolTaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("currency-async-");
        executor.initialize();
        return executor;
    }
}
