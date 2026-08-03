package com.example.account.ms_account_reservation;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import com.example.currency.CurrencyClient;
import com.example.currency.CurrencyService;
import com.example.currency.ConvertResponse;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class CurrencyCachingIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7").withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @MockitoBean
    CurrencyClient currencyClient;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    MeterRegistry meterRegistry;

    @Test
    void secondCall_servedFromCache_apiCalledOnce() {

        when(currencyClient.convert(anyString(), anyString(), anyString()))
                .thenReturn(new ConvertResponse(true, new BigDecimal("0.87")));

        currencyService.getExchangeRate("USD", "EUR");
        currencyService.getExchangeRate("USD", "EUR");

        verify(currencyClient, times(1))
                .convert(anyString(), anyString(), anyString());
        double apiCalls = meterRegistry.get("currency.exchange.rate.requests").counter().count();
        assertEquals(1.0, apiCalls);
    }
}
