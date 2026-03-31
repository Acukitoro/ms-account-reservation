package com.example.account.ms_account_reservation.repository;

import com.example.account.ms_account_reservation.model.AccountStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountStatusRepository extends JpaRepository<AccountStatusEntity, Integer> {
}
