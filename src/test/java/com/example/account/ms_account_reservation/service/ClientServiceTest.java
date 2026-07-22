package com.example.account.ms_account_reservation.service;

import com.example.account.ms_account_reservation.dto.ClientExistsResponse;
import com.example.account.ms_account_reservation.dto.ClientRequestDto;
import com.example.account.ms_account_reservation.dto.ClientResponseDto;
import com.example.account.ms_account_reservation.dto.ClientUpdateRequestDto;
import com.example.account.ms_account_reservation.exception.ClientAlreadyExistsException;
import com.example.account.ms_account_reservation.exception.ClientNotFoundException;
import com.example.account.ms_account_reservation.model.ClientEntity;
import com.example.account.ms_account_reservation.model.ClientStatus;
import com.example.account.ms_account_reservation.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    ClientRepository repository;

    @InjectMocks
    ClientService service;

    @Test
    void create_whenMdmCodeIsNew_returnsCreatedClient() {

        ClientRequestDto requestDto = new ClientRequestDto(
                "Test User",
                "RU",
                "Classic",
                "N12345",
                "S1",
                "ID",
                12L
        );

        ClientEntity savedEntity = ClientEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Test User")
                .citizenship("RU")
                .clientType("Classic")
                .documentNumber("N12345")
                .documentSeries("S1")
                .documentType("ID")
                .mdmCode(12L)
                .status(ClientStatus.ACTIVE)
                .build();


        when(repository.findByMdmCode(12L))
                .thenReturn(Optional.empty());

        when(repository.save(any()))
                .thenReturn(savedEntity);

        ClientResponseDto result = service.create(requestDto);

        assertEquals(12L, result.getMdmCode());
        assertEquals("Test User", result.getFullName());
        assertEquals(ClientResponseDto.StatusEnum.ACTIVE, result.getStatus());
    }

    @Test
    void create_whenMdmCodeExists_throwsConflict() {

        ClientRequestDto requestDto = new ClientRequestDto(
                "Test User",
                "RU",
                "Classic",
                "N12345",
                "S1",
                "ID",
                12L
        );

        ClientEntity existingClient = ClientEntity.builder()
                .id(UUID.randomUUID())
                .mdmCode(12L)
                .build();

        when(repository.findByMdmCode(12L))
                .thenReturn(Optional.of(existingClient));

        assertThrows(ClientAlreadyExistsException.class, () -> service.create(requestDto));
    }

    @Test
    void getClientById_whenExists_returnsClient() {

        UUID id = UUID.randomUUID();

        ClientEntity existingClient = ClientEntity.builder()
                .id(id)
                .fullName("Test User")
                .mdmCode(12L)
                .status(ClientStatus.ACTIVE)
                .build();

        when(repository.findById(id))
                .thenReturn(Optional.of(existingClient));

        ClientResponseDto result = service.getClientById(id);

        assertEquals(id, result.getId());
        assertFalse(result.getHasAccounts());
    }

    @Test
    void getClientById_whenMissing_throwsNotFound() {

        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> service.getClientById(id));
    }

    @Test
    void updateClientById_whenExists_returnsUpdatedClient() {

        UUID id = UUID.randomUUID();

        ClientUpdateRequestDto request = new ClientUpdateRequestDto();
        request.setFullName("Updated Name");
        request.setCitizenship("RU");
        request.setClientType("Classic");
        request.setDocumentNumber("N12345");
        request.setDocumentSeries("S1");
        request.setDocumentType("ID");

        ClientEntity existingClient = ClientEntity.builder()
                .id(id)
                .fullName("Test Name")
                .citizenship("CZ")
                .clientType("User")
                .documentNumber("S12345")
                .documentSeries("G2")
                .documentType("Passport")
                .status(ClientStatus.ACTIVE)
                .build();

        when(repository.findClientById(id))
                .thenReturn(Optional.of(existingClient));

        when(repository.save(any()))
                .thenReturn(existingClient);

        ClientResponseDto result = service.updateClientById(id, request);

        assertEquals(id, result.getId());
        assertEquals(request.getFullName(), result.getFullName());
    }

    @Test
    void updateClientById_whenMissing_throwsNotFound() {

        UUID id = UUID.randomUUID();

        when(repository.findClientById(id))
                .thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> service.updateClientById(id, new ClientUpdateRequestDto()));
    }

    @Test
    void deleteClientById_whenExists_marksDeleted() {

        UUID id = UUID.randomUUID();

        ClientEntity existingClient = ClientEntity.builder()
                .id(id)
                .fullName("Test Name")
                .status(ClientStatus.ACTIVE)
                .build();

        when(repository.findClientById(id))
                .thenReturn(Optional.of(existingClient));

        service.deleteClientById(id);

        assertEquals(ClientStatus.DELETED, existingClient.getStatus());
        verify(repository).save(existingClient);
    }

    @Test
    void deleteClientById_whenMissing_throwsNotFound() {

        UUID id = UUID.randomUUID();

        when(repository.findClientById(id))
                .thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> service.deleteClientById(id));
    }

    @Test
    void existsByClientId_whenExists_returnsTrue() {

        UUID id = UUID.randomUUID();

        ClientEntity existingClient = ClientEntity.builder()
                .id(id)
                .fullName("Test Name")
                .status(ClientStatus.ACTIVE)
                .build();

        when(repository.findById(id))
                .thenReturn(Optional.of(existingClient));

        ClientExistsResponse result = service.existsByClientId(id);

        assertEquals(id, result.getClientId());
        assertTrue(result.getExists());
        assertEquals(ClientExistsResponse.StatusEnum.ACTIVE, result.getStatus());
    }

    @Test
    void existsByClientId_whenMissing_returnsFalse() {

        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        ClientExistsResponse result = service.existsByClientId(id);

        assertNull(result.getStatus());
        assertFalse(result.getExists());
    }

}
