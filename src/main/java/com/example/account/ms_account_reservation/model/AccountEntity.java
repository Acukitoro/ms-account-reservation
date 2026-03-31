package com.example.account.ms_account_reservation.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "account")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "status_id")
    AccountStatusEntity status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    ClientEntity client;

    private String account_type;

    private String currency_code;
}
