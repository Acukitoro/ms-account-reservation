package com.example.account.ms_account_reservation.repository.projection;

import com.example.account.ms_account_reservation.model.ClientStatus;

import java.util.UUID;

public interface ClientListView {

    UUID getId();
    Long getMdmCode();
    String getFullName();
    ClientStatus getStatus();
    Boolean getHasAccounts();
}
