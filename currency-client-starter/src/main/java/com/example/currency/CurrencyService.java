package com.example.currency;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.math.BigDecimal;

public class CurrencyService {

    private final CurrencyClient currencyClient;
    private final CurrencyProperties properties;

    public CurrencyService(CurrencyClient currencyClient, CurrencyProperties properties) {
        this.currencyClient = currencyClient;
        this.properties = properties;
    }

    @Retryable(
            retryFor = feign.FeignException.class,
            maxAttemptsExpression = "${app.currency-client.retry.max-attempts:3}",
            backoff = @Backoff(
                    delayExpression = "${app.currency-client.retry.delay:1000}",
                    multiplierExpression = "${app.currency-client.retry.multiplier:2}")
    )
    public BigDecimal getExchangeRate(String from, String to) {

        if(from.equals(to)) {
            return BigDecimal.ONE;
        }

        ConvertResponse response = currencyClient.convert(properties.apiKey(), from, to);

        if (response == null || !response.success()) {
            throw new CurrencyClientException("Currency API вернул неуспешный ответ: " + from + " -> " + to);
        }

        return response.result();
    }
}
