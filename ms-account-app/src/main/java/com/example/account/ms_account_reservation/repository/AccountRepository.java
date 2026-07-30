package com.example.account.ms_account_reservation.repository;

import com.example.account.ms_account_reservation.model.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
}
