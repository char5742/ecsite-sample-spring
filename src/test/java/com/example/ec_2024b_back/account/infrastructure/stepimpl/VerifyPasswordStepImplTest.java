package com.example.ec_2024b_back.account.infrastructure.stepimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.account.domain.step.PasswordInput;
import com.example.ec_2024b_back.account.domain.step.VerifyPasswordStep.InvalidPasswordException;
import com.example.ec_2024b_back.utils.Fast;
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

  private String accountId = "user-id-123";
  private String hashedPassword = "hashedPassword";
  private String rawPassword = "password";

  @Test
  void apply_shouldReturnSuccessWithAccountId_whenPasswordMatches() {
    when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);
    var input = new PasswordInput(accountId, hashedPassword, rawPassword);

    var result = verifyPasswordStep.apply(input);

    assertThat(result).isEqualTo(accountId);
  }

  @Test
  void apply_shouldReturnFailureWithInvalidPasswordException_whenPasswordDoesNotMatch() {
    when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);
    var input = new PasswordInput(accountId, hashedPassword, rawPassword);

    try {
      verifyPasswordStep.apply(input);
      org.junit.jupiter.api.Assertions.fail("Expected InvalidPasswordException");
    } catch (InvalidPasswordException e) {
      // expected
    }
  }
}
