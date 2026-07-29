package com.example.account.ms_account_reservation.exception;

public class ClientAlreadyExistsException extends ClientApiException{

    public ClientAlreadyExistsException(Long mdmCode) {
        super(
                "Client with mdmCode=" + mdmCode + " already exists",
                "CLIENT_ALREADY_EXISTS",
                409
        );
    }
}
