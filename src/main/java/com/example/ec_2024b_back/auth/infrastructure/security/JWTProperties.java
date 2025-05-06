package com.example.ec_2024b_back.auth.infrastructure.security;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * JWT設定プロパティクラス 複数のモジュールで共有されるため、shareモジュールに配置
 *
 * @param secret JWTのシークレットキー
 * @param expirationMillis JWTの有効期限（ミリ秒）
 */
@Validated
@ConfigurationProperties("jwt")
public record JWTProperties(@NotNull String secret, @NotNull Long expirationMillis) {}
