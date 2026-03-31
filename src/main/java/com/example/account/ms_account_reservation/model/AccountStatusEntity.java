package com.example.account.ms_account_reservation.model;

import jakarta.persistence.*;

@Entity
@Table(name = "account_status")
public class AccountStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;
}
