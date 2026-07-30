package com.example.currency;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.currency-client")
public record CurrencyProperties(String baseUrl, String apiKey) {
}
