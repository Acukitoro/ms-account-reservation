package com.example.account.ms_account_reservation.service;

import com.example.account.ms_account_reservation.dto.*;
import com.example.account.ms_account_reservation.exception.ClientAlreadyExistsException;
import com.example.account.ms_account_reservation.exception.ClientNotFoundException;
import com.example.account.ms_account_reservation.mapper.ClientMapper;
import com.example.account.ms_account_reservation.model.ClientEntity;
import com.example.account.ms_account_reservation.model.ClientStatus;
import com.example.account.ms_account_reservation.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientResponseDto create(ClientRequestDto request) {

        log.info("Creating client with mdmCode={}", request.getMdmCode());

        if (clientRepository.findByMdmCode(request.getMdmCode()).isPresent()) {
            log.warn("Client already exists with mdmCode={}", request.getMdmCode());
            throw new ClientAlreadyExistsException(request.getMdmCode());
        }

        ClientEntity entity = clientMapper.toEntity(request);
        entity.setStatus(ClientStatus.ACTIVE);

        ClientEntity saved = clientRepository.save(entity);

        log.info("Client created successfully with id={}, mdmCode={}", saved.getId(), saved.getMdmCode());

        return clientMapper.toDto(saved);
    }

    public ClientPageResponseDto getClients(Pageable pageable, String fullName, Long mdmCode) {

        log.info("Fetching clients page={}, size={}, fullName={}, mdmCode={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                fullName,
                mdmCode
        );

        Page<ClientEntity> page;
        if (fullName != null && mdmCode != null) {
            page = clientRepository.findByFullNameContainingAndMdmCode(fullName,mdmCode, pageable);
        } else if (fullName != null) {
            page = clientRepository.findByFullNameContaining(fullName, pageable);
        } else if (mdmCode != null) {
            page = clientRepository.findByMdmCode(mdmCode, pageable);
        } else {
            page = clientRepository.findAll(pageable);
        }

        return clientMapper.toPageDto(page);
    }

    public ClientResponseDto getClientById(UUID id) {

        log.info("Fetching client by id={}", id);

        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() ->{
                    log.warn("Client not found id={}", id);
                    return new ClientNotFoundException(id);
                });

        return clientMapper.toDto(entity);
    }

    public ClientResponseDto updateClientById(UUID id, ClientUpdateRequestDto request) {

        log.info("Updating client with id={}", id);

        ClientEntity entity = clientRepository.findClientById(id)
                .orElseThrow(() ->{
                    log.warn("Client not found id={}", id);
                    return new ClientNotFoundException(id);
                });

        clientMapper.updateEntity(entity, request);

        ClientEntity updated = clientRepository.save(entity);

        log.info("Client updated successfully with id={}", id);

        return clientMapper.toDto(updated);
    }

    public void deleteClientById(UUID id) {

        log.info("Deleting client with id={}", id);

        ClientEntity entity = clientRepository.findClientById(id)
                .orElseThrow(() ->{
                    log.warn("Client not found id={}", id);
                    return new ClientNotFoundException(id);
                });

        entity.setStatus(ClientStatus.DELETED);

        clientRepository.save(entity);

        log.info("Client deleted successfully with id={}", id);
    }

    public ClientExistsResponse existsByClientId(UUID id) {

        Optional<ClientEntity> entityOpt = clientRepository.findById(id);

        if (entityOpt.isEmpty()) {
            log.info("Client exists check id={}, exists=false", id);
            return clientMapper.toExistsDto(id, null, false);
        }
        ClientEntity entity = entityOpt.get();

        log.info("Client exists check id={}, exists=true", id);

        return clientMapper.toExistsDto(id, entity.getStatus(), true);
    }
}
