package com.example.account.ms_account_reservation.service;

import com.example.account.ms_account_reservation.dto.ClientResponseDto;
import com.example.account.ms_account_reservation.dto.ClientSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientReportServiceTest {

    @Mock
    ClientService clientService;

    @Mock
    ExchangeRateService exchangeRateService;

    @Mock
    MetricsService metricsService;

    Executor executor = Executors.newFixedThreadPool(4);
    ClientReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ClientReportService(executor, clientService, exchangeRateService, metricsService);
        ReflectionTestUtils.setField(reportService, "timeoutSeconds", 3);
    }

    @Test
    void getClientSummary_whenAllCallsSucceed_mergesClientAndRates() {
        UUID id = UUID.randomUUID();

        ClientResponseDto dto = new ClientResponseDto();

        dto.setId(id);
        dto.setFullName("Test name");
        dto.setMdmCode(3L);
        dto.setStatus(ClientResponseDto.StatusEnum.ACTIVE);

        when(clientService.getClientById(id))
                .thenReturn(dto);

        when(exchangeRateService.getRate("USD", "RUB"))
                .thenReturn(new BigDecimal("80.1"));

        when(exchangeRateService.getRate("EUR", "RUB"))
                .thenReturn(new BigDecimal("90.6"));

        ClientSummaryDto result = reportService.getClientSummary(id);

        assertEquals(dto, result.getClient());
        assertEquals(
                Map.of("USD/RUB", new BigDecimal("80.1"), "EUR/RUB", new BigDecimal("90.6")),
                result.getRates());

        verify(metricsService, times(3)).recordAsyncTask();
    }

    @Test
    void getClientSummary_runsCallsInParallel() {

        CountDownLatch latch = new CountDownLatch(3);

        ClientResponseDto dto = new ClientResponseDto();
        UUID id = UUID.randomUUID();

        dto.setId(id);
        dto.setFullName("Test name");
        dto.setMdmCode(3L);
        dto.setStatus(ClientResponseDto.StatusEnum.ACTIVE);

        when(exchangeRateService.getRate("USD", "RUB"))
                .thenAnswer(inv -> {
                    latch.countDown();
                    latch.await();
                    return new BigDecimal("80.1");
                });

        when(exchangeRateService.getRate("EUR", "RUB"))
                .thenAnswer(inv -> {
                    latch.countDown();
                    latch.await();
                    return new BigDecimal("90.6");
                });

        when(clientService.getClientById(id))
                .thenAnswer(inv -> {
                    latch.countDown();
                    latch.await();
                    return dto;
                });

        ClientSummaryDto result = reportService.getClientSummary(id);
        assertEquals(dto, result.getClient());
    }
}
