package com.example.ec_2024b_back.share.infrastructure.security;

import com.example.ec_2024b_back.user.domain.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.vavr.control.Try;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/** JWT (JSON Web Token) の生成と検証を行うユーティリティクラス. */
@Component
@AllArgsConstructor
public class JsonWebTokenProvider {
  private final JWTProperties properties;

  private SecretKey getSigningKey() {
    var keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * 指定されたトークンからクレームを抽出します.
   *
   * @param token JWTトークン
   * @return クレーム
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  /**
   * 指定されたトークンから特定のクレームを抽出します.
   *
   * @param <T> クレームの型
   * @param token JWTトークン
   * @param claimsResolver クレームを解決する関数
   * @return 特定のクレーム
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final var claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * 指定されたトークンからユーザーID (subject) を抽出します.
   *
   * @param token JWTトークン
   * @return ユーザーID
   */
  public String extractUserId(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * 指定されたトークンから有効期限を抽出します.
   *
   * @param token JWTトークン
   * @return 有効期限
   */
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * トークンが期限切れかどうかを検証します.
   *
   * @param token JWTトークン
   * @return 期限切れの場合はtrue
   */
  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
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
    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + properties.getExpirationMillis()))
        .signWith(getSigningKey())
        .compact();
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
