package com.example.account.ms_account_reservation.exception;

public abstract class ClientApiException extends RuntimeException {

    private final String errorCode;
    private final int status;

    protected ClientApiException(String message, String errorCode, int status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatus() {
        return status;
    }
}
