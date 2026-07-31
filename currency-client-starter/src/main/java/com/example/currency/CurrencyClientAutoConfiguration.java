package com.example.currency;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;

@AutoConfiguration
@EnableConfigurationProperties(CurrencyProperties.class)
@EnableFeignClients
@EnableRetry
@ConditionalOnProperty(prefix = "app.currency-client", name = "enabled", havingValue = "true")
public class CurrencyClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CurrencyService currencyService(CurrencyClient currencyClient, CurrencyProperties properties) {
        return new CurrencyService(currencyClient, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(HealthIndicator.class)
    @ConditionalOnProperty(prefix = "app.currency-client", name = "health-check-enabled", havingValue = "true")
    public CurrencyHealthIndicator currencyHealthIndicator(CurrencyClient currencyClient, CurrencyProperties properties) {
        return new CurrencyHealthIndicator(currencyClient, properties);
    }
}
