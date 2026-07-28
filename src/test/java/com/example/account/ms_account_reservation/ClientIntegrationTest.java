package com.example.account.ms_account_reservation;

import com.example.account.ms_account_reservation.model.ClientEntity;
import com.example.account.ms_account_reservation.model.ClientStatus;
import com.example.account.ms_account_reservation.repository.ClientRepository;
import com.example.account.ms_account_reservation.util.TestJsonReader;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ClientIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ClientRepository repository;

    @Test
    void createClient_persistToDatabase_returns201() throws Exception {

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestJsonReader.read("json/create-client.json")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        Optional<ClientEntity> savedOpt = repository.findByMdmCode(10L);
        assertTrue(savedOpt.isPresent());
        ClientEntity saved = savedOpt.get();
        assertEquals("Test User", saved.getFullName());
    }

    @Test
    void createClient_whenExists_returns409() throws Exception {

        repository.save(ClientEntity.builder()
                .fullName("Test Name")
                .citizenship("RU")
                .clientType("Classic")
                .documentNumber("N12345678")
                .documentSeries("S2")
                .documentType("ID")
                .mdmCode(10L)
                .status(ClientStatus.ACTIVE)
                .build());

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestJsonReader.read("json/create-client.json")))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteClientById_softDeletedInDatabase() throws Exception {

        ClientEntity existing = repository.save(ClientEntity.builder()
                .fullName("Test Name")
                .citizenship("RU")
                .clientType("Classic")
                .documentNumber("N12345678")
                .documentSeries("S2")
                .documentType("ID")
                .mdmCode(20L)
                .status(ClientStatus.ACTIVE)
                .build());
        UUID id = existing.getId();

        mockMvc.perform(delete("/api/v1/clients/{clientId}", id))
                .andExpect(status().isNoContent());

        ClientEntity afterDeleted = repository.findClientById(id).orElseThrow();
        assertEquals(ClientStatus.DELETED, afterDeleted.getStatus());
    }
}
