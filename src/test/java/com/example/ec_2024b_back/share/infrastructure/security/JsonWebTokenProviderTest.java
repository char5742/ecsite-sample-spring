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
  private static final String USER_ID = "user-test-id";
  private static final String SECRET = "test-secret-key-longer-than-256-bits-for-hmac256-test";
  private static final Long EXPIRATION_MILLIS = 3600000L;

  @BeforeEach
  void setUp() {
    jwtProperties = new JWTProperties(SECRET, EXPIRATION_MILLIS);
    jsonWebTokenProvider = new JsonWebTokenProvider(jwtProperties);

    var zipcode = new Address.Zipcode("100-0000");
    var prefecture = Address.Prefecture.TOKYO;
    var municipalities = new Address.Municipalities("テスト区");
    var detailAddress = new Address.DetailAddress("テスト1-1");
    var address = new Address(zipcode, prefecture, municipalities, detailAddress);
    user =
        new User(
            new Account.AccountId(USER_ID),
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
    var validationResult = jsonWebTokenProvider.validateToken(token, USER_ID);
    validationResult.subscribe(
        result -> assertThat(result).isTrue(),
        error -> {
          throw new AssertionError("Validation failed with error", error);
        });
    assertThat(jsonWebTokenProvider.extractUserId(token)).isEqualTo(USER_ID);
  }

  @Test
  void validateToken_shouldReturnSuccessTrue_forValidTokenAndCorrectUser() {
    var token = jsonWebTokenProvider.generateToken(user);

    var result = jsonWebTokenProvider.validateToken(token, USER_ID);

    result.subscribe(
        r -> assertThat(r).isTrue(),
        e -> {
          throw new AssertionError("Validation failed with error", e);
        });
  }

  @Test
  void validateToken_shouldReturnSuccessFalse_forExpiredToken() {
    var expiredProps = new JWTProperties(SECRET, -3600000L);
    var expiredProvider = new JsonWebTokenProvider(expiredProps);
    var expiredToken = expiredProvider.generateToken(user);

    var result = jsonWebTokenProvider.validateToken(expiredToken, USER_ID);

    result
        .doOnError(
            e -> {
              assertThat(e).isInstanceOf(TokenExpiredException.class);
            })
        .subscribe();
  }

  @Test
  void validateToken_shouldReturnFailure_forInvalidSignature() {
    var token = jsonWebTokenProvider.generateToken(user);
    var invalidToken = token.substring(0, token.lastIndexOf('.') + 1) + "invalidSignature";

    var result = jsonWebTokenProvider.validateToken(invalidToken, USER_ID);

    result
        .doOnError(
            e -> {
              assertThat(e).isInstanceOf(SignatureVerificationException.class);
            })
        .subscribe();
  }

  @Test
  void validateToken_shouldReturnSuccessFalse_forWrongUser() {
    var token = jsonWebTokenProvider.generateToken(user);
    var wrongUserId = "wrong-user-id";

    var result = jsonWebTokenProvider.validateToken(token, wrongUserId);

    result.subscribe(
        r -> assertThat(r).isFalse(),
        e -> {
          throw new AssertionError("Validation failed with error", e);
        });
  }

  @Test
  void extractUserId_shouldReturnCorrectUserId() {
    var token = jsonWebTokenProvider.generateToken(user);

    var extractedUserId = jsonWebTokenProvider.extractUserId(token);

    assertThat(extractedUserId).isEqualTo(USER_ID);
  }

  @Test
  void extractExpiration_shouldReturnCorrectExpiration() {
    var now = Instant.now();
    var token = jsonWebTokenProvider.generateToken(user);

    var expiration = jsonWebTokenProvider.extractExpiration(token);

    var expectedExpiration = now.plus(EXPIRATION_MILLIS, ChronoUnit.MILLIS);
    assertThat(expiration)
        .isBetween(expectedExpiration.minusSeconds(1), expectedExpiration.plusSeconds(1));
  }
}
