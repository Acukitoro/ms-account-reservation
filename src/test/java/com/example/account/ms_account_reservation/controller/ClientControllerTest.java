package com.example.account.ms_account_reservation.controller;

import com.example.account.ms_account_reservation.dto.ClientExistsResponse;
import com.example.account.ms_account_reservation.dto.ClientPageResponseDto;
import com.example.account.ms_account_reservation.dto.ClientResponseDto;
import com.example.account.ms_account_reservation.exception.ClientAlreadyExistsException;
import com.example.account.ms_account_reservation.exception.ClientNotFoundException;
import com.example.account.ms_account_reservation.service.ClientService;
import com.example.account.ms_account_reservation.util.TestJsonReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ClientService service;

    @Test
    void createClient_whenAllArgumentsValid_returns201() throws Exception {

        UUID id = UUID.randomUUID();
        ClientResponseDto client = new ClientResponseDto();
        client.setId(id);
        client.setFullName("Test User");
        client.setMdmCode(1L);
        client.setStatus(ClientResponseDto.StatusEnum.ACTIVE);

        when(service.create(any()))
                .thenReturn(client);

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestJsonReader.read("json/create-client.json")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.mdmCode").value(1L));
    }

    @Test
    void createClient_whenMdmCodeExists_returns409() throws Exception {

        when(service.create(any()))
                .thenThrow(new ClientAlreadyExistsException(1L));

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestJsonReader.read("json/create-client.json")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.statusCode").value(409));
    }

    @ParameterizedTest
    @MethodSource("invalidPostBodies")
    void createClient_whenInvalidBodies_returns400(String body) throws Exception {

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    void getClientById_whenExists_returns200() throws Exception {

        UUID id = UUID.randomUUID();
        ClientResponseDto client = new ClientResponseDto();
        client.setId(id);
        client.setFullName("Test User");
        client.setMdmCode(1L);
        client.setStatus(ClientResponseDto.StatusEnum.ACTIVE);

        when(service.getClientById(id))
                .thenReturn(client);

        mockMvc.perform(get("/api/v1/clients/{clientId}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.mdmCode").value(1L));
    }

    @Test
    void getClientById_whenMissing_returns404() throws Exception {

        UUID id = UUID.randomUUID();

        when(service.getClientById(id))
                .thenThrow(new ClientNotFoundException(id));

        mockMvc.perform(get("/api/v1/clients/{clientId}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    void getClientById_unknownRoute_returns404() throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/unknown/{clientId}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    void getClients_whenExists_returns200() throws Exception {

        UUID id = UUID.randomUUID();
        ClientResponseDto client = new ClientResponseDto();
        client.setId(id);
        client.setFullName("Test User");
        client.setMdmCode(1L);
        client.setStatus(ClientResponseDto.StatusEnum.ACTIVE);

        ClientPageResponseDto page = new ClientPageResponseDto();
        page.setContent(List.of(client));
        page.setPage(0);
        page.setSize(20);
        page.setTotalElements(1L);
        page.setTotalPages(1);

        when(service.getClients(any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].fullName").value("Test User"))
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.totalElements").value(1));

    }

    @Test
    void updateClientById_whenExists_returns200() throws Exception {

        UUID id = UUID.randomUUID();
        ClientResponseDto client = new ClientResponseDto();
        client.setId(id);
        client.setFullName("Updated Name");
        client.setMdmCode(1L);
        client.setStatus(ClientResponseDto.StatusEnum.ACTIVE);

        when(service.updateClientById(any(), any()))
                .thenReturn(client);

        mockMvc.perform(put("/api/v1/clients/{clientId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestJsonReader.read("json/update-client.json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.fullName").value("Updated Name"));

    }

    @Test
    void updateClientById_whenMissing_returns404() throws Exception {
        UUID id = UUID.randomUUID();

        when(service.updateClientById(any(), any()))
                .thenThrow(new ClientNotFoundException(id));

        mockMvc.perform(put("/api/v1/clients/{clientId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestJsonReader.read("json/update-client.json")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @ParameterizedTest
    @MethodSource("invalidPutBodies")
    void updateClientById_whenInvalidBodies_returns400(String body) throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(put("/api/v1/clients/{clientId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    void deleteClientById_whenExists_returns204() throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/clients/{clientId}", id))
                .andExpect(status().isNoContent());

        verify(service).deleteClientById(id);
    }

    @Test
    void deleteClientById_whenMissing_returns404() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new ClientNotFoundException(id)).when(service).deleteClientById(id);

        mockMvc.perform(delete("/api/v1/clients/{clientId}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    void existsByClientId_whenExists_returns200() throws Exception {

        UUID id = UUID.randomUUID();
        ClientExistsResponse response = new ClientExistsResponse();
        response.setClientId(id);
        response.setExists(true);
        response.setStatus(ClientExistsResponse.StatusEnum.ACTIVE);

        when(service.existsByClientId(id))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/clients/{clientId}/exists", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    static Stream<String> invalidPostBodies() {
        return Stream.of(
                """
                        {"fullName": "", "citizenship": "RU", "clientType": "Classic", "documentNumber": "N12345", "documentSeries": "S1", "documentType": "ID", "mdmCode": 1}""",
                """
                        {"fullName": "Test User", "citizenship": "", "clientType": "Classic", "documentNumber": "N12345", "documentSeries": "S1", "documentType": "ID", "mdmCode": 1}""",
                """
                        {"fullName": "Test User", "citizenship": "RU", "clientType": "", "documentNumber": "N12345", "documentSeries": "S1", "documentType": "ID", "mdmCode": 1}""",
                """
                        {"fullName": "Test User", "citizenship": "RU", "clientType": "Classic", "documentNumber": "", "documentSeries": "S1", "documentType": "ID", "mdmCode": 1}""",
                """
                        {"fullName": "Test User", "citizenship": "RU", "clientType": "Classic", "documentNumber": "N12345", "documentSeries": "", "documentType": "ID", "mdmCode": 1}""",
                """
                        {"fullName": "Test User", "citizenship": "RU", "clientType": "Classic", "documentNumber": "N12345", "documentSeries": "S1", "documentType": "", "mdmCode": 1}""",
                """
                        {"fullName": "Test User", "citizenship": "RU", "clientType": "Classic", "documentNumber": "N12345", "documentSeries": "S1", "documentType": "ID", "mdmCode": null}""",
                """
                        {"fullName": "Test User", "citizenship": "RU", "clientType": "Classic", "documentNumber": "N12345", "documentSeries": "S1", "documentType": "ID", "mdmCode": -1}""",
                """
                        {"fullName": "Test User", "citizenship": "RU", "clientType": "Classic", "documentNumber": "N12345", "documentSeries": "S1", "documentType": "ID", "mdmCode":}"""
        );
    }

    static Stream<String> invalidPutBodies() {
        return Stream.of(
                """
                        {"fullName": "", "citizenship": "RU", "clientType": "Classic", "documentNumber": "N12345", "documentSeries": "S1", "documentType": "ID"}""",
                """
                        {"fullName": "Updated Name", "citizenship": "", "clientType": "Classic", "documentNumber": "N12345", "documentSeries": "S1", "documentType": "ID"}""",
                """
                        {"fullName": "Updated Name", "citizenship": "RU", "clientType": "", "documentNumber": "N12345", "documentSeries": "S1", "documentType": "ID"}""",
                """
                        {"fullName": "Updated Name", "citizenship": "RU", "clientType": "Classic", "documentNumber": "", "documentSeries": "S1", "documentType": "ID"}""",
                """
                        {"fullName": "Updated Name", "citizenship": "RU", "clientType": "Classic", "documentNumber": "N12345", "documentSeries": "", "documentType": "ID"}""",
                """
                        {"fullName": "Updated Name", "citizenship": "RU", "clientType": "Classic", "documentNumber": "N12345", "documentSeries": "S1", "documentType": ""}"""

        );
    }

}
