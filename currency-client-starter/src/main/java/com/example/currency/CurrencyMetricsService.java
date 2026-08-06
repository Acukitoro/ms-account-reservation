package com.example.currency;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public class CurrencyMetricsService {

    private final Counter apiRequestCounter;

    public CurrencyMetricsService(MeterRegistry meterRegistry) {
        this.apiRequestCounter = Counter.builder("currency.exchange.rate.requests")
                .description("Число вызовов внегнего API")
                .register(meterRegistry);
    }

    public void recordApiCall() {
        apiRequestCounter.increment();
    }
}
