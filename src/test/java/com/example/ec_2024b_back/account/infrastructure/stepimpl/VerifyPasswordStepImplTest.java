package com.example.ec_2024b_back.account.infrastructure.stepimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.account.domain.step.PasswordInput;
import com.example.ec_2024b_back.account.domain.step.VerifyPasswordStep.InvalidPasswordException;
import com.example.ec_2024b_back.utils.Fast;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@Fast
@ExtendWith(MockitoExtension.class)
class VerifyPasswordStepImplTest {

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private VerifyPasswordStepImpl verifyPasswordStep;

  private static final String ACCOUNT_ID = "user-id-123";
  private static final String HASHED_PASSWORD = "hashedPassword";
  private static final String RAW_PASSWORD = "password";

  @Test
  void apply_shouldReturnSuccessWithAccountId_whenPasswordMatches() {
    when(passwordEncoder.matches(RAW_PASSWORD, HASHED_PASSWORD)).thenReturn(true);
    var input = new PasswordInput(ACCOUNT_ID, HASHED_PASSWORD, RAW_PASSWORD);

    var result = verifyPasswordStep.apply(input);

    assertThat(result).isEqualTo(ACCOUNT_ID);
  }

  @Test
  void apply_shouldReturnFailureWithInvalidPasswordException_whenPasswordDoesNotMatch() {
    when(passwordEncoder.matches(RAW_PASSWORD, HASHED_PASSWORD)).thenReturn(false);
    var input = new PasswordInput(ACCOUNT_ID, HASHED_PASSWORD, RAW_PASSWORD);

    try {
      verifyPasswordStep.apply(input);
      Assertions.fail("Expected InvalidPasswordException");
    } catch (InvalidPasswordException e) {
      // expected
    }
  }
}
