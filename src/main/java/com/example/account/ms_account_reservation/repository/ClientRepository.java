package com.example.account.ms_account_reservation.repository;

import com.example.account.ms_account_reservation.dto.ClientUpdateRequestDto;
import com.example.account.ms_account_reservation.model.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    Optional<ClientEntity> findByMdmCode(Long mdmCode);

    Page<ClientEntity> findByFullNameContaining(String fullName, Pageable pageable);

    Page<ClientEntity> findByMdmCode(Long mdmCode, Pageable pageable);

    Page<ClientEntity> findByFullNameContainingAndMdmCode(String fullName, Long mdmCode, Pageable pageable);

    Optional<ClientEntity> findClientById(UUID id);

}
