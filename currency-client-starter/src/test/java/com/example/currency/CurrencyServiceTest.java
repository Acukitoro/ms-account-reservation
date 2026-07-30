package com.example.currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    RestTemplate restTemplate;

    CurrencyProperties properties = new CurrencyProperties("https://api.exchangerate.host", "test-key");
    CurrencyService service;

    @BeforeEach
    void setUp() {
        service = new CurrencyService(restTemplate, properties);
    }

    @Test
    void getExchangeRate_returnBigDecimalsResult() {

        when(restTemplate.getForObject(anyString(), eq(ConvertResponse.class)))
                .thenReturn(new ConvertResponse(true, new BigDecimal("0.87235")));

        BigDecimal result = service.getExchangeRate("USD", "EUR");

        assertEquals(new BigDecimal("0.87235"), result);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(ConvertResponse.class));
        assertTrue(urlCaptor.getValue().contains("from=USD"));
        assertTrue(urlCaptor.getValue().contains("to=EUR"));
    }

    @Test
    void getExchangeRate_whenApuUnsuccessful_throwsException() {

        when(restTemplate.getForObject(anyString(), eq(ConvertResponse.class)))
                .thenReturn(new ConvertResponse(false, null));

        assertThrows(CurrencyClientException.class, () -> service.getExchangeRate("USD", "EUR"));
    }
}
