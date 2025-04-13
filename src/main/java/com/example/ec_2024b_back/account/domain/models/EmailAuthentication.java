package com.example.ec_2024b_back.account.domain.models;

import com.example.ec_2024b_back.share.domain.models.Email;

public record EmailAuthentication(Email email, String password) implements IAuthentication {
  private static final AuthenticationId id = new AuthenticationId("email");

  @Override
  public AuthenticationId id() {
    return id;
  }
}
