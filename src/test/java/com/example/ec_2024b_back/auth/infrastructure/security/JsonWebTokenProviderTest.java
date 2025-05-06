package com.example.ec_2024b_back.auth.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.utils.Fast;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Fast
@ExtendWith(MockitoExtension.class)
class JsonWebTokenProviderTest {

  @Mock(lenient = true)
  private JWTProperties properties;

  @InjectMocks private JsonWebTokenProvider tokenProvider;

  private static final String TEST_SECRET =
      "testsecrettestsecrettestsecrettestsecrettestsecrettestsecret";
  private static final long TEST_EXPIRATION_MILLIS = 3600000L; // 1時間
  private final UUID accountId = UUID.randomUUID();

  private void setupMocks() {
    when(properties.secret()).thenReturn(TEST_SECRET);
    when(properties.expirationMillis()).thenReturn(TEST_EXPIRATION_MILLIS);
  }

  @Test
  void generateToken_shouldCreateToken_whenAccountProvided() {
    // Arrange
    setupMocks();
    var account = mock(Account.class);
    when(account.getId()).thenReturn(new AccountId(accountId));

    // Act
    String token = tokenProvider.generateToken(account);

    // Assert
    assertThat(token).isNotNull().isNotEmpty();
  }

  @Test
  void extractUserId_shouldReturnUserId_whenValidTokenProvided() {
    // Arrange
    setupMocks();
    var account = mock(Account.class);
    when(account.getId()).thenReturn(new AccountId(accountId));
    String token = tokenProvider.generateToken(account);

    // Act
    String extractedUserId = tokenProvider.extractUserId(token);

    // Assert
    assertThat(extractedUserId).isEqualTo(accountId.toString());
  }

  @Test
  void extractExpiration_shouldReturnFutureDate_whenValidTokenProvided() {
    // Arrange
    setupMocks();
    var account = mock(Account.class);
    when(account.getId()).thenReturn(new AccountId(accountId));
    String token = tokenProvider.generateToken(account);

    // Act
    Instant expiration = tokenProvider.extractExpiration(token);

    // Assert
    assertThat(expiration).isAfter(Instant.now());
    // 期限は現在から約1時間後 (少し余裕を持たせてチェック)
    assertThat(expiration).isBefore(Instant.now().plusMillis(TEST_EXPIRATION_MILLIS + 10000));
  }

  @Test
  void validateToken_shouldReturnTrue_whenValidTokenAndCorrectUserIdProvided() {
    // Arrange
    setupMocks();
    var account = mock(Account.class);
    when(account.getId()).thenReturn(new AccountId(accountId));
    String token = tokenProvider.generateToken(account);

    // Act
    Mono<Boolean> validationResult = tokenProvider.validateToken(token, accountId.toString());

    // Assert
    StepVerifier.create(validationResult).expectNext(true).verifyComplete();
  }

  @Test
  void validateToken_shouldReturnFalse_whenValidTokenButIncorrectUserIdProvided() {
    // Arrange
    setupMocks();
    var account = mock(Account.class);
    when(account.getId()).thenReturn(new AccountId(accountId));
    String token = tokenProvider.generateToken(account);
    var wrongUserId = UUID.randomUUID().toString();

    // Act
    Mono<Boolean> validationResult = tokenProvider.validateToken(token, wrongUserId);

    // Assert
    StepVerifier.create(validationResult).expectNext(false).verifyComplete();
  }

  @Test
  void validateToken_shouldReturnError_whenInvalidTokenProvided() {
    // Arrange
    setupMocks();
    var invalidToken = "invalid.jwt.token";

    // Act & Assert
    StepVerifier.create(tokenProvider.validateToken(invalidToken, accountId.toString()))
        .expectError()
        .verify();
  }
}
