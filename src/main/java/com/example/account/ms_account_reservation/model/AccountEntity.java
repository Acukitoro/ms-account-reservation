package com.example.account.ms_account_reservation.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "account")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private AccountStatusEntity status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "currency_code")
    private String currencyCode;

    private AccountEntity(Builder builder) {
        this.status = builder.status;
        this.client = builder.client;
        this.accountType = builder.accountType;
        this.currencyCode = builder.currencyCode;
    }

    public AccountEntity() {}

    public UUID getId() {
        return id;
    }

    public AccountStatusEntity getStatus() {
        return status;
    }

    public void setStatus(AccountStatusEntity status) {
        this.status = status;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public static class Builder {
        private AccountStatusEntity status;
        private ClientEntity client;
        private String accountType;
        private String currencyCode;

        public Builder status(AccountStatusEntity status) {
            this.status = status;
            return this;
        }

        public Builder client(ClientEntity client) {
            this.client = client;
            return this;
        }

        public Builder accountType(String accountType) {
            this.accountType = accountType;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public AccountEntity build() {
            return new AccountEntity(this);
        }

    }
}
