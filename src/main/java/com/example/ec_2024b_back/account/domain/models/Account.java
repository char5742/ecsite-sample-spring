package com.example.ec_2024b_back.account.domain.models;

import com.google.common.collect.ImmutableList;

public record Account(AccountId id, ImmutableList<IAuthentication> authentications) {
  public record AccountId(String id) {
    public AccountId {
      if (id.isBlank()) {
        throw new IllegalArgumentException("ID must not be blank");
      }
    }
  }
}
