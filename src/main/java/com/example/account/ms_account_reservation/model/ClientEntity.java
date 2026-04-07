package com.example.account.ms_account_reservation.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "citizenship")
    private String citizenship;

    @Column(name = "client_type")
    private String clientType;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "document_series")
    private String documentSeries;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "mdm_code")
    private Long mdmCode;
}
