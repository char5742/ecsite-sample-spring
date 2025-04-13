package com.example.ec_2024b_back.share.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.ec_2024b_back.user.domain.models.User;
import io.vavr.control.Try;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/** JWT (JSON Web Token) の生成と検証を行うユーティリティクラス. */
@Component
@AllArgsConstructor
public class JsonWebTokenProvider {
  private final JWTProperties properties;

  private Algorithm getAlgorithm() {
    return Algorithm.HMAC256(properties.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  /**
   * トークンをデコードします。
   *
   * @param token JWTトークン
   * @return デコードされたJWT
   */
  private DecodedJWT decodeToken(String token) {
    return JWT.require(getAlgorithm()).build().verify(token);
  }

  /**
   * 指定されたトークンからユーザーID (subject) を抽出します.
   *
   * @param token JWTトークン
   * @return ユーザーID
   */
  public String extractUserId(String token) {
    return decodeToken(token).getSubject();
  }

  /**
   * 指定されたトークンから有効期限を抽出します.
   *
   * @param token JWTトークン
   * @return 有効期限のInstant
   */
  public Instant extractExpiration(String token) {
    return decodeToken(token).getExpiresAtAsInstant();
  }

  /**
   * トークンが期限切れかどうかを検証します.
   *
   * @param token JWTトークン
   * @return 期限切れの場合はtrue
   */
  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).isBefore(Instant.now());
  }

  /**
   * ユーザー情報に基づいてJWTトークンを生成します.
   *
   * @param user Userドメインモデル
   * @return 生成されたJWTトークン
   */
  public String generateToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, user.id().id());
  }

  /**
   * クレームとサブジェクトに基づいてJWTトークンを作成します.
   *
   * @param claims クレーム
   * @param subject サブジェクト (通常はユーザーID)
   * @return 作成されたJWTトークン
   */
  private String createToken(Map<String, Object> claims, String subject) {
    Instant now = Instant.now();
    Instant expiry = now.plus(properties.getExpirationMillis(), ChronoUnit.MILLIS);

    return JWT.create()
        .withPayload(claims)
        .withSubject(subject)
        .withIssuedAt(now)
        .withExpiresAt(expiry)
        .sign(getAlgorithm());
  }

  /**
   * トークンが有効かどうかを検証します (ユーザーIDの一致と有効期限). VavrのTryを使用して結果を返します.
   *
   * @param token JWTトークン
   * @param userId 検証対象のユーザーID
   * @return トークンが有効な場合はTry.success(true)、無効な場合はTry.success(false) または Try.failure
   */
  public Try<Boolean> validateToken(String token, String userId) {
    return Try.of(
            () -> {
              final var extractedUserId = extractUserId(token);
              return (extractedUserId.equals(userId) && !isTokenExpired(token));
            })
        .onFailure(e -> System.err.println("Token validation failed: " + e.getMessage()));
  }
}
