package com.example.ec_2024b_back.auth.domain.models;

/**
 * JWTトークン
 *
 * @param value JWTトークン
 */
public record JsonWebToken(String value) {
  public JsonWebToken {
    if (value.isEmpty()) {
      throw new IllegalArgumentException("JWTトークンはnullまたは空であってはいけません");
    }
  }
}
