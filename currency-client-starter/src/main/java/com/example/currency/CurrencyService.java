package com.example.currency;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

public class CurrencyService {

    private final RestTemplate restTemplate;
    private final CurrencyProperties properties;

    public CurrencyService(RestTemplate restTemplate, CurrencyProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public BigDecimal getExchangeRate(String from, String to) {

        String url = UriComponentsBuilder.fromHttpUrl(properties.baseUrl())
                .path("/convert")
                .queryParam("access_key", properties.apiKey())
                .queryParam("from", from)
                .queryParam("to", to)
                .toUriString();

        ConvertResponse response = restTemplate.getForObject(url, ConvertResponse.class);

        if (response == null || !response.success()) {
            throw new CurrencyClientException("Currency API вернул неуспешный ответ: " + from + " -> " + to);
        }

        return response.result();
    }
}
