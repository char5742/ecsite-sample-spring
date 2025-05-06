package com.example.ec_2024b_back.auth.infrastructure.stepimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow;
import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.EmailAuthentication;
import com.example.ec_2024b_back.share.domain.models.Email;
import com.example.ec_2024b_back.utils.Fast;
import com.google.common.collect.ImmutableList;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.test.StepVerifier;

@Fast
@ExtendWith(MockitoExtension.class)
class VerifyWithPasswordStepImplTest {

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private VerifyWithPasswordStepImpl verifyWithPasswordStep;

  @Test
  void apply_shouldReturnVerifiedContext_whenValidPasswordProvided() {
    // Arrange
    var email = new Email("test@example.com");
    var rawPassword = "raw-password";
    var hashedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyno12345678901234567890123abc";
    var password = new EmailAuthentication.HashedPassword(hashedPassword);
    var authentication = new EmailAuthentication(email, password);
    var uuid = UUID.randomUUID();
    var account = Account.reconstruct(new AccountId(uuid), ImmutableList.of(authentication));
    var context = new LoginWorkflow.Context.Founded(account, rawPassword);

    when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);

    // Act & Assert
    StepVerifier.create(verifyWithPasswordStep.apply(context))
        .assertNext(
            verified -> {
              assertThat(verified).isNotNull();
              assertThat(verified.account()).isEqualTo(account);
            })
        .verifyComplete();
  }

  @Test
  void apply_shouldThrowInvalidPasswordException_whenInvalidPasswordProvided() {
    // Arrange
    var email = new Email("test@example.com");
    var rawPassword = "wrong-password";
    var hashedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyno12345678901234567890123abc";
    var password = new EmailAuthentication.HashedPassword(hashedPassword);
    var authentication = new EmailAuthentication(email, password);
    var uuid = UUID.randomUUID();
    var account = Account.reconstruct(new AccountId(uuid), ImmutableList.of(authentication));
    var context = new LoginWorkflow.Context.Founded(account, rawPassword);

    when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);

    // Act & Assert
    StepVerifier.create(verifyWithPasswordStep.apply(context))
        .expectError(LoginWorkflow.InvalidPasswordException.class)
        .verify();
  }

  @Test
  void apply_shouldThrowNoEmailAuthenticationException_whenNoEmailAuthenticationExists() {
    // Arrange
    var uuid = UUID.randomUUID();
    var account = Account.reconstruct(new AccountId(uuid), ImmutableList.of()); // 空の認証リスト
    var context = new LoginWorkflow.Context.Founded(account, "password");

    // Act & Assert
    StepVerifier.create(verifyWithPasswordStep.apply(context))
        .expectError(LoginWorkflow.NoEmailAuthenticationException.class)
        .verify();
  }
}
