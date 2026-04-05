package com.example.account.ms_account_reservation.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "client")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "citizenship")
    private String citizenship;

    @Column(name = "client_type")
    private String clientType;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "document_series")
    private String documentSeries;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "mdm_code")
    private Long mdmCode;

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentSeries() {
        return documentSeries;
    }

    public void setDocumentSeries(String documentSeries) {
        this.documentSeries = documentSeries;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Long getMdmCode() {
        return mdmCode;
    }

    public void setMdmCode(Long mdmCode) {
        this.mdmCode = mdmCode;
    }

    public ClientEntity() {}

    private ClientEntity(Builder builder) {
        this.fullName = builder.fullName;
        this.citizenship = builder.citizenship;
        this.clientType = builder.clientType;
        this.documentNumber = builder.documentNumber;
        this.documentSeries = builder.documentSeries;
        this.documentType = builder.documentType;
        this.mdmCode = builder.mdmCode;

    }

    public static class Builder {
        private String fullName;
        private String citizenship;
        private String clientType;
        private String documentNumber;
        private String documentSeries;
        private String documentType;
        private Long mdmCode;

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder citizenship(String citizenship) {
            this.citizenship = citizenship;
            return this;
        }

        public Builder clientType(String clientType) {
            this.clientType = clientType;
            return this;
        }

        public Builder documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        public Builder documentSeries(String documentSeries) {
            this.documentSeries = documentSeries;
            return this;
        }

        public Builder documentType(String documentType) {
            this.documentType = documentType;
            return this;
        }

        public Builder mdmCode(Long mdmCode) {
            this.mdmCode = mdmCode;
            return this;
        }

        public ClientEntity build() {
            return new ClientEntity(this);
        }
    }
}
