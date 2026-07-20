package com.example.account.ms_account_reservation.mapper;

import com.example.account.ms_account_reservation.dto.*;
import com.example.account.ms_account_reservation.model.ClientEntity;
import com.example.account.ms_account_reservation.model.ClientStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public class ClientMapper {

    public static ClientEntity toEntity(ClientRequestDto request) {
        return ClientEntity.builder()
                .fullName(request.getFullName())
                .citizenship(request.getCitizenship())
                .clientType(request.getClientType())
                .documentNumber(request.getDocumentNumber())
                .documentSeries(request.getDocumentSeries())
                .documentType(request.getDocumentType())
                .mdmCode(request.getMdmCode())
                .build();
    }

    public static ClientResponseDto toDto(ClientEntity entity) {
        ClientResponseDto dto = new ClientResponseDto();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setMdmCode(entity.getMdmCode());
        dto.setStatus(
                ClientResponseDto.StatusEnum.fromValue(entity.getStatus().name())
        );
        return dto;
    }

    public static ClientPageResponseDto toPageDto(Page<ClientEntity> page) {

        List<ClientResponseDto> content = page.getContent().stream()
                .map(ClientMapper::toDto)
                .toList();

        ClientPageResponseDto dto = new ClientPageResponseDto();
        dto.setContent(content);
        dto.setPage(page.getNumber());
        dto.setSize(page.getSize());
        dto.setTotalElements(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());

        return dto;
    }

    public static void updateEntity(ClientEntity entity ,ClientUpdateRequestDto request) {
        entity.setFullName(request.getFullName());
        entity.setCitizenship(request.getCitizenship());
        entity.setClientType(request.getClientType());
        entity.setDocumentNumber(request.getDocumentNumber());
        entity.setDocumentSeries(request.getDocumentSeries());
        entity.setDocumentType(request.getDocumentType());
    }

    public static ClientExistsResponse toExistsDto(UUID id, ClientStatus status, boolean exists) {
        ClientExistsResponse dto = new ClientExistsResponse();
        dto.setExists(exists);
        dto.setClientId(id);

        if (status != null) {
            dto.setStatus(ClientExistsResponse.StatusEnum.fromValue(status.name()));
        }

        return dto;
    }
}
