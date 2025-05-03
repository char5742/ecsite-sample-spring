package com.example.ec_2024b_back.auth.domain.models;

import com.example.ec_2024b_back.auth.domain.models.EmailAuthentication.HashedPassword;
import com.example.ec_2024b_back.share.domain.models.Email;
import java.util.Map;

public sealed interface Authentication permits EmailAuthentication {
  String type();

  static Authentication of(String type, Map<String, String> credential) {
    return switch (type) {
      case EmailAuthentication.TYPE -> {
        var email = credential.get("email");
        var password = credential.get("password");
        if (email == null || password == null) {
          throw new IllegalArgumentException("Email認証は email と password の両方のフィールドを必要とします");
        }
        yield new EmailAuthentication(new Email(email), new HashedPassword(password));
      }
      default -> throw new IllegalArgumentException("不明な認証方法のタイプ: " + type);
    };
  }
}
