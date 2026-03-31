package com.example.account.ms_account_reservation.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "client")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String full_name;

    private String citizenship;

    private String client_type;

    private String document_number;

    private String document_series;

    private String document_type;

    private Long mdm_code;
}
