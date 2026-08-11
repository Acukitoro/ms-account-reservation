package com.example.account.ms_account_reservation.exception;

public class ReportTimeoutException extends ClientApiException{
    public ReportTimeoutException() {
        super("Report request timeout", "GATEWAY_TIMEOUT", 504);
    }
}
