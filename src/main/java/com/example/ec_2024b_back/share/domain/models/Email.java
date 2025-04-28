package com.example.ec_2024b_back.share.domain.models;

public record Email(String value) {
  public Email {
    if (value.isEmpty()) {
      throw new IllegalArgumentException("メールアドレスは空にできません");
    }
    if (!value.matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$")) {
      throw new IllegalArgumentException("メールアドレスのフォーマットが不正です");
    }
  }
}
