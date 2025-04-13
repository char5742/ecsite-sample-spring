package com.example.ec_2024b_back.share.domain.models;

public record Email(String email) {
  public Email {
    if (email.isEmpty()) {
      throw new IllegalArgumentException("メールアドレスは空にできません");
    }
    if (!email.matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$")) {
      throw new IllegalArgumentException("メールアドレスのフォーマットが不正です");
    }
  }
}
