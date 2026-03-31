package com.example.account.ms_account_reservation.repository;

import com.example.account.ms_account_reservation.model.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {
}
