package com.example.ec_2024b_back.account.infrastructure.stepimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.account.domain.models.Account;
import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.share.domain.models.Address.Zipcode;
import com.example.ec_2024b_back.share.infrastructure.security.JsonWebTokenProvider;
import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.utils.Fast;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Fast
class GenerateJwtTokenStepImplTest {

  @Mock private JsonWebTokenProvider jsonWebTokenProvider;

  @InjectMocks private GenerateJwtTokenStepImpl generateJwtTokenStep;

  private final User testUser =
      new User(
          new Account.AccountId("test-id"),
          "Taro",
          "Yamada",
          new Address(
              new Zipcode("100-0000"),
              Address.Prefecture.TOKYO,
              new Address.Municipalities("Chiyoda"),
              new Address.DetailAddress("1-1-1")),
          "090-1234-5678");
  private final String expectedToken = "generated.jwt.token";

  @Test
  void apply_shouldReturnSuccessWithToken_whenGenerationSucceeds() {
    // Arrange
    when(jsonWebTokenProvider.generateToken(any(User.class))).thenReturn(expectedToken);

    // Act
    var result = generateJwtTokenStep.apply(testUser);

    // Assert
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.get()).isEqualTo(expectedToken);
  }

  @Test
  void apply_shouldReturnFailure_whenGenerationThrowsException() {
    // Arrange
    var exception = new RuntimeException("Token generation error");
    when(jsonWebTokenProvider.generateToken(any(User.class))).thenThrow(exception);

    // Act
    var result = generateJwtTokenStep.apply(testUser);

    // Assert
    assertThat(result.isFailure()).isTrue();
    assertThat(result.getCause()).isEqualTo(exception);
  }
}
