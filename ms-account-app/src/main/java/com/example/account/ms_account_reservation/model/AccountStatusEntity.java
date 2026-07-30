package com.example.account.ms_account_reservation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account_status")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}
