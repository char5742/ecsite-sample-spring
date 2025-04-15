package com.example.ec_2024b_back.share.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.ec_2024b_back.account.domain.models.Account;
import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.utils.Fast;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Fast
class JsonWebTokenProviderTest {

  private JsonWebTokenProvider jsonWebTokenProvider;
  private JWTProperties jwtProperties;
  private User user;
  private String userId = "user-test-id";
  private String secret = "test-secret-key-longer-than-256-bits-for-hmac256-test";
  private Long expirationMillis = 3600000L;

  @BeforeEach
  void setUp() {
    jwtProperties = new JWTProperties(secret, expirationMillis);
    jsonWebTokenProvider = new JsonWebTokenProvider(jwtProperties);

    var zipcode = new Address.Zipcode("100-0000");
    var prefecture = Address.Prefecture.TOKYO;
    var municipalities = new Address.Municipalities("テスト区");
    var detailAddress = new Address.DetailAddress("テスト1-1");
    var address = new Address(zipcode, prefecture, municipalities, detailAddress);
    user =
        new User(
            new Account.AccountId(userId),
            "Test",
            "User",
            address,
            "000-0000-0000",
            "dummy-password");
  }

  @Test
  void generateToken_shouldCreateValidToken() {
    var token = jsonWebTokenProvider.generateToken(user);

    assertThat(token).isNotNull().isNotEmpty();
    var validationResult = jsonWebTokenProvider.validateToken(token, userId);
    assertThat(validationResult.isSuccess()).isTrue();
    assertThat(validationResult.get()).isTrue();
    assertThat(jsonWebTokenProvider.extractUserId(token)).isEqualTo(userId);
  }

  @Test
  void validateToken_shouldReturnSuccessTrue_forValidTokenAndCorrectUser() {
    var token = jsonWebTokenProvider.generateToken(user);

    var result = jsonWebTokenProvider.validateToken(token, userId);

    assertThat(result.isSuccess()).isTrue();
    assertThat(result.get()).isTrue();
  }

  @Test
  void validateToken_shouldReturnSuccessFalse_forExpiredToken() {
    var expiredProps = new JWTProperties(secret, -3600000L);
    var expiredProvider = new JsonWebTokenProvider(expiredProps);
    var expiredToken = expiredProvider.generateToken(user);

    var result = jsonWebTokenProvider.validateToken(expiredToken, userId);

    assertThat(result.isFailure()).isTrue();
    assertThat(result.getCause()).isInstanceOf(TokenExpiredException.class);
  }

  @Test
  void validateToken_shouldReturnFailure_forInvalidSignature() {
    var token = jsonWebTokenProvider.generateToken(user);
    var invalidToken = token.substring(0, token.lastIndexOf('.') + 1) + "invalidSignature";

    var result = jsonWebTokenProvider.validateToken(invalidToken, userId);

    assertThat(result.isFailure()).isTrue();
    assertThat(result.getCause()).isInstanceOf(SignatureVerificationException.class);
  }

  @Test
  void validateToken_shouldReturnSuccessFalse_forWrongUser() {
    var token = jsonWebTokenProvider.generateToken(user);
    var wrongUserId = "wrong-user-id";

    var result = jsonWebTokenProvider.validateToken(token, wrongUserId);

    assertThat(result.isSuccess()).isTrue();
    assertThat(result.get()).isFalse();
  }

  @Test
  void extractUserId_shouldReturnCorrectUserId() {
    var token = jsonWebTokenProvider.generateToken(user);

    var extractedUserId = jsonWebTokenProvider.extractUserId(token);

    assertThat(extractedUserId).isEqualTo(userId);
  }

  @Test
  void extractExpiration_shouldReturnCorrectExpiration() {
    var now = Instant.now();
    var token = jsonWebTokenProvider.generateToken(user);

    var expiration = jsonWebTokenProvider.extractExpiration(token);

    var expectedExpiration = now.plus(expirationMillis, ChronoUnit.MILLIS);
    assertThat(expiration)
        .isBetween(expectedExpiration.minusSeconds(1), expectedExpiration.plusSeconds(1));
  }
}
