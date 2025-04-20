package com.example.ec_2024b_back.share.infrastructure.security;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@AllArgsConstructor
@ConfigurationProperties("jwt")
public class JWTProperties {
  /** JWTのシークレットキー */
  @NotNull private final String secret;

  /** JWTの有効期限（ミリ秒） */
  @NotNull private final Long expirationMillis;
}
