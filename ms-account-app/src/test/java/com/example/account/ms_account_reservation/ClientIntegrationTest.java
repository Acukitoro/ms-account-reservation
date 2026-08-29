package com.example.account.ms_account_reservation;

import com.example.account.ms_account_reservation.model.AccountEntity;
import com.example.account.ms_account_reservation.model.AccountStatusEntity;
import com.example.account.ms_account_reservation.model.ClientEntity;
import com.example.account.ms_account_reservation.model.ClientStatus;
import com.example.account.ms_account_reservation.repository.AccountRepository;
import com.example.account.ms_account_reservation.repository.AccountStatusRepository;
import com.example.account.ms_account_reservation.repository.ClientRepository;
import com.example.account.ms_account_reservation.util.TestJsonReader;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    ClientRepository clientRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountStatusRepository accountStatusRepository;

    @Autowired
    EntityManager entityManager;

    private ClientEntity saveClientWithAccount(long mdmCode, AccountStatusEntity status) {

        ClientEntity client = clientRepository.save(ClientEntity.builder()
                .fullName("Test Name")
                .citizenship("RU")
                .clientType("Classic")
                .documentNumber("N" + mdmCode)
                .documentSeries("S1")
                .documentType("ID")
                .mdmCode(mdmCode)
                .status(ClientStatus.ACTIVE)
                .build());

        accountRepository.save(AccountEntity.builder()
                .client(client)
                .status(status)
                .accountType("Deposit")
                .currencyCode("USD")
                .accountNumber("0012345")
                .balance(new BigDecimal("120.00"))
                .build());

        return client;
    }

    @Test
    void createClient_persistToDatabase_returns201() throws Exception {

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestJsonReader.read("json/create-client.json")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        Optional<ClientEntity> savedOpt = clientRepository.findByMdmCode(10L);
        assertTrue(savedOpt.isPresent());
        ClientEntity saved = savedOpt.get();
        assertEquals("Test User", saved.getFullName());
    }

    @Test
    void createClient_whenExists_returns409() throws Exception {

        clientRepository.save(ClientEntity.builder()
                .fullName("Test Name")
                .citizenship("RU")
                .clientType("Classic")
                .documentNumber("N12345678")
                .documentSeries("S2")
                .documentType("ID")
                .mdmCode(10L)
                .status(ClientStatus.ACTIVE)
                .build());

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestJsonReader.read("json/create-client.json")))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteClientById_softDeletedInDatabase() throws Exception {

        ClientEntity existing = clientRepository.save(ClientEntity.builder()
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

        mockMvc.perform(delete("/clients/{clientId}", id))
                .andExpect(status().isNoContent());

        ClientEntity afterDeleted = clientRepository.findClientById(id).orElseThrow();
        assertEquals(ClientStatus.DELETED, afterDeleted.getStatus());
    }

    @Test
    void getClientById_whenHasAccounts_returnsClientWithAccounts() throws Exception {

        AccountStatusEntity status = accountStatusRepository.findById(1).orElseThrow();

        ClientEntity client = saveClientWithAccount(20L, status);

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/clients/{clientId}", client.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasAccounts").value(true))
                .andExpect(jsonPath("$.accounts", hasSize(1)))
                .andExpect(jsonPath("$.accounts[0].currencyCode").value("USD"))
                .andExpect(jsonPath("$.accounts[0].status.name").value("NEW"))
                .andExpect(jsonPath("$.accounts[0].accountNumber").value("0012345"))
                .andExpect(jsonPath("$.accounts[0].balance").value(120.00));
    }

    @Test
    void getClients_whenClientsHaveAccounts_returnsHasAccountsTrue() throws Exception {

        AccountStatusEntity status = accountStatusRepository.findById(1).orElseThrow();

        for (long i = 1; i <= 3; i++) {
            saveClientWithAccount(100 + i, status);
        }

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[*].hasAccounts", everyItem(is(true))));
    }
}
