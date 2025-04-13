package com.example.ec_2024b_back.account.domain.models;

import java.util.List;

public record Account(AccountId id, List<IAuthentication> authentications) {
  public record AccountId(String id) {
    public AccountId {
      if (id.isBlank()) {
        throw new IllegalArgumentException("ID must not be blank");
      }
    }
  }
}
