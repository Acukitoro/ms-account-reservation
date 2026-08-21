package com.example.account.ms_account_reservation.mapper;

import com.example.account.ms_account_reservation.dto.*;
import com.example.account.ms_account_reservation.model.ClientEntity;
import com.example.account.ms_account_reservation.model.ClientStatus;
import com.example.account.ms_account_reservation.model.AccountEntity;
import com.example.account.ms_account_reservation.model.AccountStatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    ClientEntity toEntity(ClientRequestDto request);

    @Mapping(target="hasAccounts", expression="java(hasAccounts(entity))")
    ClientResponseDto toDto(ClientEntity entity);

    default ClientPageResponseDto toPageDto(Page<ClientEntity> page) {

        List<ClientResponseDto> content = page.getContent().stream()
                .map(this::toDto)
                .toList();

        ClientPageResponseDto dto = new ClientPageResponseDto();
        dto.setContent(content);
        dto.setPage(page.getNumber());
        dto.setSize(page.getSize());
        dto.setTotalElements(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());

        return dto;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "mdmCode", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    void updateEntity(@MappingTarget ClientEntity entity , ClientUpdateRequestDto request);

    default ClientExistsResponse toExistsDto(UUID id, ClientStatus status, boolean exists) {
        ClientExistsResponse dto = new ClientExistsResponse();
        dto.setExists(exists);
        dto.setClientId(id);

        if (status != null) {
            dto.setStatus(ClientExistsResponse.StatusEnum.fromValue(status.name()));
        }

        return dto;
    }
    @Mapping(target="hasAccounts", expression="java(hasAccounts(entity))")
    ClientDetailsResponseDto toDetailsDto(ClientEntity entity);

    AccountDto toAccountDto(AccountEntity account);

    AccountStatusDto toAccountStatusDto(AccountStatusEntity status);

    default boolean hasAccounts(ClientEntity entity) {
        return entity.getAccounts() != null && !entity.getAccounts().isEmpty();
    }

}
