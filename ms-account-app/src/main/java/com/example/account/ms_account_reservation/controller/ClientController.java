package com.example.account.ms_account_reservation.controller;

import com.example.account.ms_account_reservation.api.ClientsApi;
import com.example.account.ms_account_reservation.dto.*;
import com.example.account.ms_account_reservation.service.ClientReportService;
import com.example.account.ms_account_reservation.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClientController implements ClientsApi {

    private final ClientService clientService;
    private final ClientReportService reportService;

    @Override
    public ResponseEntity<ClientResponseDto> createClient(
            @Valid @RequestBody ClientRequestDto request
    ) {
        ClientResponseDto response = clientService.create(request);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<ClientPageResponseDto> getClients(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "mdmCode", required = false) Long mdmCode
    ) {
        Pageable pageable = PageRequest.of(page, size);
        ClientPageResponseDto response = clientService.getClients(pageable, fullName, mdmCode);
        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<ClientDetailsResponseDto> getClientById(
            @PathVariable(value = "clientId") UUID clientId
    ) {
        ClientDetailsResponseDto response = clientService.getClientById(clientId);
        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<ClientResponseDto> updateClientById(
            @PathVariable("clientId") UUID clientId,
            @Valid @RequestBody ClientUpdateRequestDto request
            ) {

        ClientResponseDto response = clientService.updateClientById(clientId, request);
        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteClientById(
            @PathVariable(value = "clientId") UUID clientId
    ) {
        clientService.deleteClientById(clientId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ClientExistsResponse> existsByClientId(
            @PathVariable(value = "clientId") UUID clientId
    ) {
        ClientExistsResponse response = clientService.existsByClientId(clientId);
        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<ClientSummaryDto> getClientSummary(
            @PathVariable(value = "clientId") UUID id
    ) {
        ClientSummaryDto response = reportService.getClientSummary(id);
        return ResponseEntity.status(200).body(response);
    }
}
