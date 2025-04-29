package com.example.ec_2024b_back.auth.domain.workflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.step.CreateAccountWithEmailStep;
import com.example.ec_2024b_back.auth.domain.step.CreateAccountWithEmailStep.EmailWithPasswordInput;
import com.example.ec_2024b_back.auth.domain.step.FindAccountByEmailStep;
import com.example.ec_2024b_back.auth.domain.workflow.SignupWorkflow.EmailAlreadyExistsException;
import com.example.ec_2024b_back.share.domain.models.Email;
import com.example.ec_2024b_back.utils.Fast;
import com.google.common.collect.ImmutableList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Fast
class SignupWorkflowTest {
  private FindAccountByEmailStep findAccountByEmailStep;
  private CreateAccountWithEmailStep createAccountWithEmailStep;
  private SignupWorkflow signupWorkflow;

  @BeforeEach
  void setUp() {
    findAccountByEmailStep = mock(FindAccountByEmailStep.class);
    createAccountWithEmailStep = mock(CreateAccountWithEmailStep.class);
    signupWorkflow = new SignupWorkflow(findAccountByEmailStep, createAccountWithEmailStep);
  }

  @Test
  void execute_shouldReturnAccount_whenEmailNotExists() {
    // Given
    var email = "new@example.com";
    var password = "password";
    var uuid = UUID.randomUUID();
    var expectedAccount = Account.reconstruct(new Account.AccountId(uuid), ImmutableList.of());

    when(findAccountByEmailStep.apply(new Email(email))).thenReturn(Mono.empty());
    when(createAccountWithEmailStep.apply(any(EmailWithPasswordInput.class)))
        .thenReturn(Mono.just(expectedAccount));

    // When
    var result = signupWorkflow.execute(email, password);

    // Then
    StepVerifier.create(result).expectNext(expectedAccount).verifyComplete();
  }

  @Test
  void execute_shouldThrowEmailAlreadyExistsException_whenEmailExists() {
    // Given
    var email = "existing@example.com";
    var password = "password";
    var uuid = UUID.randomUUID();
    var existingAccount = Account.reconstruct(new Account.AccountId(uuid), ImmutableList.of());

    when(findAccountByEmailStep.apply(new Email(email))).thenReturn(Mono.just(existingAccount));

    // When
    var result = signupWorkflow.execute(email, password);

    // Then
    StepVerifier.create(result).expectError(EmailAlreadyExistsException.class).verify();
  }

  @Test
  void execute_shouldPropagateError_whenCreateAccountFails() {
    // Given
    var email = "new@example.com";
    var password = "password";
    var error = new RuntimeException("アカウント作成エラー");

    when(findAccountByEmailStep.apply(new Email(email))).thenReturn(Mono.empty());
    when(createAccountWithEmailStep.apply(any(EmailWithPasswordInput.class)))
        .thenReturn(Mono.error(error));

    // When
    var result = signupWorkflow.execute(email, password);

    // Then
    StepVerifier.create(result)
        .expectErrorMatches(
            throwable ->
                throwable instanceof RuntimeException
                    && throwable.getMessage().equals("アカウント作成エラー"))
        .verify();
  }

  @Test
  void execute_shouldThrowIllegalArgumentException_whenEmailIsInvalid() {
    // Given
    var invalidEmail = "invalid-email";
    var password = "password";

    // When & Then
    try {
      signupWorkflow.execute(invalidEmail, password);
      // 例外がスローされなかった場合はテストを失敗させる
      org.junit.jupiter.api.Assertions.fail("IllegalArgumentExceptionがスローされるべきです");
    } catch (IllegalArgumentException e) {
      // 期待通りの例外がスローされた
      org.assertj.core.api.Assertions.assertThat(e.getMessage()).contains("メールアドレスのフォーマットが不正です");
    }
  }
}
