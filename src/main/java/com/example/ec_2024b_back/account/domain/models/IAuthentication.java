package com.example.ec_2024b_back.account.domain.models;

sealed interface IAuthentication permits EmailAuthentication {
  AuthenticationId id();

  record AuthenticationId(String id) {
    public AuthenticationId {
      if (id == null || id.isBlank()) {
        throw new IllegalArgumentException("ID must not be null or blank");
      }
    }
  }
}
