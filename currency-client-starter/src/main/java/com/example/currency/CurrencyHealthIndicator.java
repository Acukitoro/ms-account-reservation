package com.example.currency;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class CurrencyHealthIndicator implements HealthIndicator {

    private final CurrencyClient currencyClient;
    private final CurrencyProperties currencyProperties;

    public CurrencyHealthIndicator(CurrencyClient currencyClient, CurrencyProperties currencyProperties) {
        this.currencyClient = currencyClient;
        this.currencyProperties = currencyProperties;
    }

    @Override
    public Health health() {

        try {
            ConvertResponse response = currencyClient.convert(currencyProperties.apiKey(), "USD", "EUR");
            if (response != null && response.success()) {
                return Health.up().withDetail("currency-api", "reachable").build();
            }
            return Health.down().withDetail("currency-api", "unsuccessful response").build();
        } catch (Exception e) {
            return Health.down().withDetail("currency-api", "unreachable").withException(e).build();
        }
    }
}
