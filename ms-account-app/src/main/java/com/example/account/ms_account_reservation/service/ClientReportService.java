package com.example.account.ms_account_reservation.service;

import com.example.account.ms_account_reservation.dto.ClientResponseDto;
import com.example.account.ms_account_reservation.dto.ClientSummaryDto;
import com.example.account.ms_account_reservation.exception.ClientApiException;
import com.example.account.ms_account_reservation.exception.ReportTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientReportService {

    @Qualifier("currencyExecutor")
    private final Executor currencyExecutor;
    private final ClientService clientService;
    private final ExchangeRateService exchangeRateService;
    private final MetricsService metricsService;

    @Value("${app.async.timeout-seconds}")
    private int timeoutSeconds;

    public ClientSummaryDto getClientSummary(UUID id) {

        CompletableFuture<ClientResponseDto> clientF =  CompletableFuture.supplyAsync(() -> {
            metricsService.recordAsyncTask();
            return clientService.getClientById(id);
        }, currencyExecutor);

        CompletableFuture<BigDecimal> usdF = CompletableFuture.supplyAsync(() -> {
            metricsService.recordAsyncTask();
            return exchangeRateService.getRate("USD", "RUB");
        }, currencyExecutor);

        CompletableFuture<BigDecimal> eurF = CompletableFuture.supplyAsync(() -> {
            metricsService.recordAsyncTask();
            return exchangeRateService.getRate("EUR", "RUB");
        }, currencyExecutor);

        try {
            CompletableFuture.allOf(clientF, usdF, eurF).get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new ReportTimeoutException();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ClientApiException businessEx) {
                throw businessEx;
            }
            throw new RuntimeException(e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ReportTimeoutException();
        }


        return new ClientSummaryDto()
                .client(clientF.join())
                .rates(Map.of("USD/RUB", usdF.join(), "EUR/RUB", eurF.join()));
    }

}
