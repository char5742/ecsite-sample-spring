package com.example.ec_2024b_back.account.infrastructure.stepimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.account.domain.models.Account;
import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.share.infrastructure.security.JsonWebTokenProvider;
import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.utils.Fast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Fast
@ExtendWith(MockitoExtension.class)
class GenerateJwtTokenStepImplTest {

  @Mock private JsonWebTokenProvider jsonWebTokenProvider;

  @InjectMocks private GenerateJwtTokenStepImpl generateJwtTokenStep;

  private User user;
  private String expectedToken = "dummy-jwt-token";

  @BeforeEach
  void setUp() {
    var zipcode = new Address.Zipcode("100-0000");
    var prefecture = Address.Prefecture.TOKYO;
    var municipalities = new Address.Municipalities("千代田区");
    var detailAddress = new Address.DetailAddress("1-1-1");
    var address = new Address(zipcode, prefecture, municipalities, detailAddress);
    user =
        new User(
            new Account.AccountId("user-id-123"),
            "Taro",
            "Yamada",
            address,
            "090-1234-5678",
            "dummy-password");
  }

  @Test
  void apply_shouldReturnSuccessWithToken_whenProviderSucceeds() {
    when(jsonWebTokenProvider.generateToken(any(User.class))).thenReturn(expectedToken);

    var result = generateJwtTokenStep.apply(user);

    assertThat(result).isEqualTo(expectedToken);
  }

  @Test
  void apply_shouldReturnFailure_whenProviderThrowsException() {
    var exception = new RuntimeException("Token generation error");
    when(jsonWebTokenProvider.generateToken(any(User.class))).thenThrow(exception);

    try {
      generateJwtTokenStep.apply(user);
      org.junit.jupiter.api.Assertions.fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertThat(e).isEqualTo(exception);
    }
  }
}
