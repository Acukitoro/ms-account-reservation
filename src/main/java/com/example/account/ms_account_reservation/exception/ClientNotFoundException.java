package com.example.account.ms_account_reservation.exception;

import java.util.UUID;

public class ClientNotFoundException extends ClientApiException{

    public ClientNotFoundException(UUID id) {
        super(
                "Client with id=" + id + " not found",
                "CLIENT_NOT_FOUND",
                404
        );
    }
}
