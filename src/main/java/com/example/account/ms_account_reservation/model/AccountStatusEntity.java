package com.example.account.ms_account_reservation.model;

import jakarta.persistence.*;

@Entity
@Table(name = "account_status")
public class AccountStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AccountStatusEntity() {}

    private AccountStatusEntity(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
    }

    public static class Builder {
        private String name;
        private String description;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public AccountStatusEntity build() {
            return new AccountStatusEntity(this);
        }
    }
}
