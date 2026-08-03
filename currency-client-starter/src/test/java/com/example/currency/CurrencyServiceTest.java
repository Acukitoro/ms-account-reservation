package com.example.currency;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    CurrencyClient currencyClient;

    CurrencyProperties properties = new CurrencyProperties("https://api.exchangerate.host", "test-key");
    CurrencyService service;

    @BeforeEach
    void setUp() {
        service = new CurrencyService(currencyClient, properties, new SimpleMeterRegistry());
    }

    @Test
    void getExchangeRate_returnBigDecimalsResult() {

        when(currencyClient.convert(anyString(), anyString(), anyString()))
                .thenReturn(new ConvertResponse(true, new BigDecimal("0.87235")));

        BigDecimal result = service.getExchangeRate("USD", "EUR");

        assertEquals(new BigDecimal("0.87235"), result);
        verify(currencyClient).convert("test-key", "USD", "EUR");
    }

    @Test
    void getExchangeRate_whenApuUnsuccessful_throwsException() {

        when(currencyClient.convert(anyString(), anyString(), anyString()))
                .thenReturn(new ConvertResponse(false, null));

        assertThrows(CurrencyClientException.class, () -> service.getExchangeRate("USD", "EUR"));
    }

    @Test
    void getExchangeRate_whenSameCurrency_returnOneWithoutApiCall() {

        BigDecimal result = service.getExchangeRate("USD", "USD");

        assertEquals(BigDecimal.ONE, result);
        verify(currencyClient, never()).convert(anyString(), anyString(), anyString());
    }
}
