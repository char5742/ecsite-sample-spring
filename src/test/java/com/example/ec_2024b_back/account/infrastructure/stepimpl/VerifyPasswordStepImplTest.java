package com.example.ec_2024b_back.account.infrastructure.stepimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.account.domain.step.VerifyPasswordStep;
import com.example.ec_2024b_back.utils.Fast;
import io.vavr.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@Fast
class VerifyPasswordStepImplTest {

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private VerifyPasswordStepImpl verifyPasswordStep;

  private final String accountId = "test-account-id";
  private final String rawPassword = "rawPassword123";
  private final String hashedPassword = "hashedPasswordXYZ";

  @BeforeEach
  void setUp() {}

  @Test
  void apply_shouldReturnSuccess_whenPasswordMatches() {
    // Arrange
    when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);
    var input = Tuple.of(accountId, hashedPassword, rawPassword);

    // Act
    var result = verifyPasswordStep.apply(input);

    // Assert
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.get()).isEqualTo(accountId);
  }

  @Test
  void apply_shouldReturnFailure_whenPasswordDoesNotMatch() {
    // Arrange
    when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);
    var input = Tuple.of(accountId, hashedPassword, rawPassword);

    // Act
    var result = verifyPasswordStep.apply(input);

    // Assert
    assertThat(result.isFailure()).isTrue();
    assertThat(result.getCause()).isInstanceOf(VerifyPasswordStep.InvalidPasswordException.class);
  }
}
