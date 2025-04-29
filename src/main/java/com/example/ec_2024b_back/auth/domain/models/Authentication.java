package com.example.ec_2024b_back.auth.domain.models;

import com.example.ec_2024b_back.auth.domain.models.EmailAuthentication.HashedPassword;
import com.example.ec_2024b_back.share.domain.models.Email;
import java.util.Map;

public sealed interface Authentication permits EmailAuthentication {
  String type();

  static Authentication of(String type, Map<String, String> credential) {
    return switch (type) {
      case EmailAuthentication.TYPE -> {
        if (!credential.containsKey("email") || !credential.containsKey("password")) {
          throw new IllegalArgumentException("Email認証は email と password の両方のフィールドを必要とします");
        }
        yield new EmailAuthentication(
            new Email(credential.get("email")), new HashedPassword(credential.get("password")));
      }
      default -> throw new IllegalArgumentException("不明な認証方法のタイプ: " + type);
    };
  }
}
