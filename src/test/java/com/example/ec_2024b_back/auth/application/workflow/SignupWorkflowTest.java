package com.example.ec_2024b_back.auth.application.workflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.auth.application.workflow.SignupWorkflow.CheckExistsEmailStep;
import com.example.ec_2024b_back.auth.application.workflow.SignupWorkflow.Context;
import com.example.ec_2024b_back.auth.application.workflow.SignupWorkflow.CreateAccountWithEmailStep;
import com.example.ec_2024b_back.auth.application.workflow.SignupWorkflow.EmailAlreadyExistsException;
import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.share.domain.models.Email;
import com.example.ec_2024b_back.utils.Fast;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Fast
class SignupWorkflowTest {
  private CheckExistsEmailStep checkExistsEmailStep;
  private CreateAccountWithEmailStep createAccountWithEmailStep;
  private SignupWorkflow signupWorkflow;

  @BeforeEach
  void setUp() {
    checkExistsEmailStep = mock(CheckExistsEmailStep.class);
    createAccountWithEmailStep = mock(CreateAccountWithEmailStep.class);
    // Create a test implementation of SignupWorkflow
    signupWorkflow =
        new SignupWorkflow() {
          @Override
          public Mono<Account> execute(Email email, String password) {
            return Mono.just(new Context.Input(email, password))
                .flatMap(checkExistsEmailStep)
                .flatMap(createAccountWithEmailStep)
                .map(Context.Created::account);
          }
        };
  }

  @Test
  void execute_shouldReturnAccount_whenEmailNotExists() {
    // Given
    var emailStr = "new@example.com";
    var email = new Email(emailStr);
    var password = "password";
    var uuid = UUID.randomUUID();
    var expectedAccount = Account.reconstruct(new AccountId(uuid), List.of());

    when(checkExistsEmailStep.apply(any(Context.Input.class)))
        .thenReturn(Mono.just(new Context.Checked(email, password)));
    when(createAccountWithEmailStep.apply(any(Context.Checked.class)))
        .thenReturn(Mono.just(new Context.Created(expectedAccount)));

    // When
    var result = signupWorkflow.execute(email, password);

    // Then
    StepVerifier.create(result).expectNext(expectedAccount).verifyComplete();
  }

  @Test
  void execute_shouldThrowEmailAlreadyExistsException_whenEmailExists() {
    // Given
    var emailStr = "existing@example.com";
    var email = new Email(emailStr);
    var password = "password";

    when(checkExistsEmailStep.apply(any(Context.Input.class)))
        .thenReturn(Mono.error(new EmailAlreadyExistsException(email)));

    // When
    var result = signupWorkflow.execute(email, password);

    // Then
    StepVerifier.create(result).expectError(EmailAlreadyExistsException.class).verify();
  }

  @Test
  void execute_shouldPropagateError_whenCreateAccountFails() {
    // Given
    var emailStr = "new@example.com";
    var email = new Email(emailStr);
    var password = "password";
    var error = new RuntimeException("アカウント作成エラー");

    when(checkExistsEmailStep.apply(any(Context.Input.class)))
        .thenReturn(Mono.just(new Context.Checked(email, password)));
    when(createAccountWithEmailStep.apply(any(Context.Checked.class)))
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
    try {
      // Eメールの作成時点で例外が発生するはず
      var invalidEmail = new Email("invalid-email");
      var password = "password";

      // When & Then
      signupWorkflow.execute(invalidEmail, password);
      // 例外がスローされなかった場合はテストを失敗させる
      org.junit.jupiter.api.Assertions.fail("IllegalArgumentExceptionがスローされるべきです");
    } catch (IllegalArgumentException e) {
      // 期待通りの例外がスローされた
      org.assertj.core.api.Assertions.assertThat(e.getMessage()).contains("メールアドレスのフォーマットが不正です");
    }
  }
}
