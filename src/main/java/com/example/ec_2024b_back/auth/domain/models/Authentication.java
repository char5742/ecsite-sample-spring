package com.example.ec_2024b_back.auth.domain.models;

import com.example.ec_2024b_back.share.domain.models.Email;
import java.util.Map;

public sealed interface Authentication permits EmailAuthentication {
  AuthenticationType type();

  record AuthenticationType(String type) {}

  static Authentication of(String type, Map<String, String> credential) {
    return switch (type) {
      case "email" ->
          new EmailAuthentication(
              new AuthenticationType("email"),
              new Email(credential.get("email")),
              credential.get("password"));
      default -> throw new IllegalArgumentException("Unknown authentication method type: " + type);
    };
  }
}
