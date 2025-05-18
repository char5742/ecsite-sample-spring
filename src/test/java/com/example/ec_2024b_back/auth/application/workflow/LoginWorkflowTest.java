package com.example.ec_2024b_back.auth.application.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow.Context;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow.FindAccountByEmailStep;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow.GenerateJWTStep;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow.VerifyWithPasswordStep;
import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.JsonWebToken;
import com.example.ec_2024b_back.share.domain.models.Email;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class LoginWorkflowTest {
  private FindAccountByEmailStep findAccountByEmailStep;
  private VerifyWithPasswordStep verifyWithPasswordStep;
  private GenerateJWTStep generateJwtStep;
  private LoginWorkflow loginWorkflow;

  @BeforeEach
  void setUp() {
    findAccountByEmailStep = mock(FindAccountByEmailStep.class);
    verifyWithPasswordStep = mock(VerifyWithPasswordStep.class);
    generateJwtStep = mock(GenerateJWTStep.class);
    // Create a test implementation of LoginWorkflow
    loginWorkflow =
        new LoginWorkflow() {
          @Override
          public Mono<Context.AccountWithJwt> execute(Email email, String password) {
            return Mono.just(new Context.Input(email, password))
                .flatMap(findAccountByEmailStep)
                .flatMap(verifyWithPasswordStep)
                .flatMap(generateJwtStep);
          }
        };
  }

  @Test
  void execute_shouldReturnJWT_whenValidCredentialsProvided() {
    var emailStr = "test@example.com";
    var email = new Email(emailStr);
    var password = "pass";
    var uuid = UUID.fromString("758c0389-b861-443f-98b2-f4c8ac89d1f4");
    var account = Account.reconstruct(new AccountId(uuid), List.of());
    var jwt = new JsonWebToken("jwt-token");

    when(findAccountByEmailStep.apply(any(Context.Input.class)))
        .thenReturn(Mono.just(new Context.Founded(account, password)));
    when(verifyWithPasswordStep.apply(any(Context.Founded.class)))
        .thenReturn(Mono.just(new Context.Verified(account)));
    when(generateJwtStep.apply(any(Context.Verified.class)))
        .thenReturn(Mono.just(new Context.AccountWithJwt(account, jwt)));

    var result = loginWorkflow.execute(email, password);
    StepVerifier.create(result)
        .assertNext(
            accountWithJwt -> {
              assertThat(accountWithJwt.account()).isEqualTo(account);
              assertThat(accountWithJwt.jwt()).isEqualTo(jwt);
            })
        .verifyComplete();
  }

  @Test
  void execute_shouldThrowUserNotFoundException_whenEmailNotFound() {
    var emailStr = "notfound@example.com";
    var email = new Email(emailStr);
    when(findAccountByEmailStep.apply(any(Context.Input.class)))
        .thenReturn(Mono.error(new LoginWorkflow.UserNotFoundException(emailStr)));

    var result = loginWorkflow.execute(email, "pass");
    StepVerifier.create(result).expectError(LoginWorkflow.UserNotFoundException.class).verify();
  }

  @Test
  void execute_shouldThrowInvalidPasswordException_whenPasswordMismatch() {
    var emailStr = "test@example.com";
    var email = new Email(emailStr);
    var password = "wrong";
    var uuid = UUID.fromString("758c0389-b861-443f-98b2-f4c8ac89d1f4");
    var account = Account.reconstruct(new AccountId(uuid), List.of());

    when(findAccountByEmailStep.apply(any(Context.Input.class)))
        .thenReturn(Mono.just(new Context.Founded(account, password)));
    when(verifyWithPasswordStep.apply(any(Context.Founded.class)))
        .thenReturn(Mono.error(new LoginWorkflow.InvalidPasswordException()));

    var result = loginWorkflow.execute(email, password);
    StepVerifier.create(result).expectError(LoginWorkflow.InvalidPasswordException.class).verify();
  }

  @Test
  void execute_shouldThrowNoEmailAuthenticationException_whenNoEmailAuthExists() {
    var emailStr = "test@example.com";
    var email = new Email(emailStr);
    var password = "pass";
    var uuid = UUID.fromString("758c0389-b861-443f-98b2-f4c8ac89d1f4");
    var account = Account.reconstruct(new AccountId(uuid), List.of());

    when(findAccountByEmailStep.apply(any(Context.Input.class)))
        .thenReturn(Mono.just(new Context.Founded(account, password)));
    when(verifyWithPasswordStep.apply(any(Context.Founded.class)))
        .thenReturn(Mono.error(new LoginWorkflow.NoEmailAuthenticationException()));

    var result = loginWorkflow.execute(email, password);
    StepVerifier.create(result)
        .expectError(LoginWorkflow.NoEmailAuthenticationException.class)
        .verify();
  }
}
