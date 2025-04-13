package com.example.ec_2024b_back.share.infrastructure.security;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Configuration("jwt")
public class JWTProperties {
  /** JWTのシークレットキー */
  @NotNull private String secret;

  /** JWTの有効期限（ミリ秒） */
  @NotNull private Long expirationMillis;
}
