package com.example.account.ms_account_reservation.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;

    private static final String ASYNC_TASKS_METRIC = "currency.async.tasks";

    public void recordAsyncTask() {
        meterRegistry.counter(ASYNC_TASKS_METRIC).increment();
    }
}
