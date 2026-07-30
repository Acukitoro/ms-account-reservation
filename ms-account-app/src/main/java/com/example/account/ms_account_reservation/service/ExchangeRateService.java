package com.example.account.ms_account_reservation.service;

import com.example.account.ms_account_reservation.exception.CurrencyServiceException;
import com.example.currency.CurrencyClientException;
import com.example.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeRateService {

    private final CurrencyService currencyService;

    public BigDecimal getRate(String fromCurrency, String toCurrency) {

        try {
            return currencyService.getExchangeRate(fromCurrency, toCurrency);
        } catch (CurrencyServiceException | CurrencyClientException e) {
            log.error("Currency API call failed: {} -> {}", fromCurrency, toCurrency, e);
            throw new CurrencyServiceException("Не удалось получить курс валют", e);
        }
    }
}
