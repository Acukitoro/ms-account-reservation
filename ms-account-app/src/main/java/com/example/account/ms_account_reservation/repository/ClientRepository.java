package com.example.account.ms_account_reservation.repository;

import com.example.account.ms_account_reservation.model.ClientEntity;
import com.example.account.ms_account_reservation.repository.projection.ClientListView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    Optional<ClientEntity> findByMdmCode(Long mdmCode);

    Optional<ClientEntity> findClientById(UUID id);

    /**
     * Страница клиентов одним запросом: hasAccounts вычисляется коррелированным EXISTS-подзапросом без N + 1 (не грузим счета по каждому клиенту)
     */
    @Query("""
            select c.id as id, c.mdmCode as mdmCode, c.fullName as fullName, c.status as status,
            (case when exists (select 1 from AccountEntity a where a.client = c) then true else false end) as hasAccounts
            from ClientEntity c
            where (:fullName is null or c.fullName like concat('%', :fullName, '%'))
                and (:mdmCode is null or c.mdmCode = :mdmCode)
            """)
    Page<ClientListView> findPageWithAccountFlag(@Param("fullName") String fullName,
                                                 @Param("mdmCode") Long mdmCode,
                                                 Pageable pageable);

    /**
     * Клиент + его счета + статусы счетов одним запросом через EntityGraph (fetch-join) вместо 2 запросов - один, без N + 1
     */
    @EntityGraph(attributePaths = {"accounts", "accounts.status"})
    Optional<ClientEntity> findWithAccountsById(UUID id);
}
