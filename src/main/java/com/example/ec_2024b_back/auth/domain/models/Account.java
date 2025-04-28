package com.example.ec_2024b_back.auth.domain.models;

import com.google.common.collect.ImmutableList;

public record Account(AccountId id, ImmutableList<Authentication> authentications) {
  public record AccountId(String id) {
    public AccountId {
      if (id.isBlank()) {
        throw new IllegalArgumentException("ID must not be blank");
      }
    }
  }
}
