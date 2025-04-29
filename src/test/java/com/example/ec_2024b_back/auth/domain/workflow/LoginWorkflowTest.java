package com.example.ec_2024b_back.auth.domain.workflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.JsonWebToken;
import com.example.ec_2024b_back.auth.domain.step.FindAccountByEmailStep;
import com.example.ec_2024b_back.auth.domain.step.GenerateJWTStep;
import com.example.ec_2024b_back.auth.domain.step.VerifyWithPasswordStep;
import com.example.ec_2024b_back.auth.domain.step.VerifyWithPasswordStep.PasswordInput;
import com.example.ec_2024b_back.share.domain.models.Email;
import com.google.common.collect.ImmutableList;
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
    loginWorkflow =
        new LoginWorkflow(findAccountByEmailStep, verifyWithPasswordStep, generateJwtStep);
  }

  @Test
  void execute_shouldReturnJWT_whenValidCredentialsProvided() {
    var email = "test@example.com";
    var password = "pass";
    var uuid = UUID.fromString("758c0389-b861-443f-98b2-f4c8ac89d1f4");
    var account = Account.reconstruct(new Account.AccountId(uuid), ImmutableList.of());
    var jwt = new JsonWebToken("jwt-token");
    when(findAccountByEmailStep.apply(new Email(email))).thenReturn(Mono.just(account));
    when(verifyWithPasswordStep.apply(any(PasswordInput.class))).thenReturn(Mono.just(account));
    when(generateJwtStep.apply(account)).thenReturn(Mono.just(jwt));

    var result = loginWorkflow.execute(email, password);
    StepVerifier.create(result).expectNext(jwt).verifyComplete();
  }

  @Test
  void execute_shouldThrowUserNotFoundException_whenEmailNotFound() {
    var email = "notfound@example.com";
    when(findAccountByEmailStep.apply(new Email(email))).thenReturn(Mono.empty());
    var result = loginWorkflow.execute(email, "pass");
    StepVerifier.create(result).expectError(LoginWorkflow.UserNotFoundException.class).verify();
  }

  @Test
  void execute_shouldThrowInvalidPasswordException_whenPasswordMismatch() {
    var email = "test@example.com";
    var uuid = UUID.fromString("758c0389-b861-443f-98b2-f4c8ac89d1f4");
    var account = Account.reconstruct(new Account.AccountId(uuid), ImmutableList.of());
    when(findAccountByEmailStep.apply(new Email(email))).thenReturn(Mono.just(account));
    when(verifyWithPasswordStep.apply(any(PasswordInput.class)))
        .thenReturn(Mono.error(new VerifyWithPasswordStep.InvalidPasswordException()));
    var result = loginWorkflow.execute(email, "wrong");
    StepVerifier.create(result)
        .expectError(VerifyWithPasswordStep.InvalidPasswordException.class)
        .verify();
  }

  @Test
  void execute_shouldThrowNoEmailAuthenticationException_whenNoEmailAuthExists() {
    var email = "test@example.com";
    var uuid = UUID.fromString("758c0389-b861-443f-98b2-f4c8ac89d1f4");
    var account = Account.reconstruct(new Account.AccountId(uuid), ImmutableList.of());
    when(findAccountByEmailStep.apply(new Email(email))).thenReturn(Mono.just(account));
    when(verifyWithPasswordStep.apply(any(PasswordInput.class)))
        .thenReturn(Mono.error(new VerifyWithPasswordStep.NoEmailAuthenticationException()));
    var result = loginWorkflow.execute(email, "pass");
    StepVerifier.create(result)
        .expectError(VerifyWithPasswordStep.NoEmailAuthenticationException.class)
        .verify();
  }
}
