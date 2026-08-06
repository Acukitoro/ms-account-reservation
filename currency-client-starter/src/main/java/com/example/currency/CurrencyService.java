package com.example.currency;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.math.BigDecimal;

public class CurrencyService {

    private final CurrencyClient currencyClient;
    private final CurrencyProperties properties;
    private final CurrencyMetricsService metricsService;

    public CurrencyService(CurrencyClient currencyClient, CurrencyProperties properties, CurrencyMetricsService metricsService) {
        this.currencyClient = currencyClient;
        this.properties = properties;
        this.metricsService = metricsService;
    }

    @Retryable(
            retryFor = feign.FeignException.class,
            maxAttemptsExpression = "${app.currency-client.retry.max-attempts:3}",
            backoff = @Backoff(
                    delayExpression = "${app.currency-client.retry.delay:1000}",
                    multiplierExpression = "${app.currency-client.retry.multiplier:2}")
    )
    @Cacheable(value = "exchangeRates", key = "#from + '-' + #to")
    public BigDecimal getExchangeRate(String from, String to) {

        if(from.equals(to)) {
            return BigDecimal.ONE;
        }

        metricsService.recordApiCall();

        ConvertResponse response = currencyClient.convert(properties.apiKey(), from, to);

        if (response == null || !response.success()) {
            throw new CurrencyClientException("Currency API вернул неуспешный ответ: " + from + " -> " + to);
        }

        return response.result();
    }
}
