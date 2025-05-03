package com.example.ec_2024b_back.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.auth.application.usecase.LoginUsecase.AuthenticationFailedException;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow.Context.AccountWithJwt;
import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.JsonWebToken;
import com.example.ec_2024b_back.share.domain.models.Email;
import com.example.ec_2024b_back.utils.Fast;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Fast
@ExtendWith(MockitoExtension.class)
class LoginUsecaseTest {

  @Mock private LoginWorkflow loginWorkflow;

  @InjectMocks private LoginUsecase loginUsecase;

  private Email email;
  private String password;

  @BeforeEach
  void setUp() {
    email = new Email("test@example.com");
    password = "password";
  }

  @Test
  void execute_shouldReturnJsonWebToken_whenWorkflowSucceeds() {
    var account = mock(Account.class);
    when(account.getDomainEvents()).thenReturn(ImmutableList.of());
    var expectedToken = new AccountWithJwt(account, new JsonWebToken("dummy-jwt-token"));
    when(loginWorkflow.execute(any(Email.class), anyString())).thenReturn(Mono.just(expectedToken));

    // ApplicationEventPublisher mock
    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    loginUsecase = new LoginUsecase(loginWorkflow, eventPublisher);

    var resultMono = loginUsecase.execute(email, password);
    StepVerifier.create(resultMono)
        .assertNext(
            token -> {
              assertThat(token).isNotNull();
              assertThat(token.value()).isEqualTo(expectedToken.jwt().value());
            })
        .verifyComplete();
  }

  @Test
  void execute_shouldThrowAuthenticationFailedException_whenWorkflowFails() {
    var cause = new RuntimeException("Workflow error");
    when(loginWorkflow.execute(any(Email.class), anyString())).thenReturn(Mono.error(cause));

    // ApplicationEventPublisher mock
    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    loginUsecase = new LoginUsecase(loginWorkflow, eventPublisher);

    var resultMono = loginUsecase.execute(email, password);
    StepVerifier.create(resultMono)
        .expectErrorMatches(
            throwable ->
                throwable instanceof AuthenticationFailedException
                    && throwable.getMessage().contains("Workflow error")
                    && throwable.getCause() == cause)
        .verify();
  }
}
