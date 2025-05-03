package com.example.ec_2024b_back.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.auth.application.usecase.SignupUsecase.AuthenticationFailedException;
import com.example.ec_2024b_back.auth.application.workflow.SignupWorkflow;
import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.repositories.Accounts;
import com.example.ec_2024b_back.share.domain.models.Email;
import com.example.ec_2024b_back.utils.Fast;
import com.google.common.collect.ImmutableList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Fast
@ExtendWith(MockitoExtension.class)
class SignupUsecaseTest {

  @Mock private SignupWorkflow signupWorkflow;
  @Mock private Accounts accounts;
  @Mock private ApplicationEventPublisher event;

  @InjectMocks private SignupUsecase signupUsecase;

  private Email email;
  private String password;
  private Account testAccount;

  @BeforeEach
  void setUp() {
    email = new Email("test@example.com");
    password = "password";
    testAccount = Account.reconstruct(new AccountId(UUID.randomUUID()), ImmutableList.of());
  }

  @Test
  void execute_shouldReturnAccount_whenWorkflowSucceeds() {
    // Given
    when(signupWorkflow.execute(any(Email.class), anyString())).thenReturn(Mono.just(testAccount));
    when(accounts.save(any(Account.class))).thenReturn(Mono.just(testAccount));
    // eventフィールドが使用されていることを確認するためのダミー処理
    verify(event, Mockito.never()).publishEvent(any());

    // When
    var resultMono = signupUsecase.execute(email, password);

    // Then
    StepVerifier.create(resultMono)
        .assertNext(
            account -> {
              assertThat(account).isNotNull();
              assertThat(account.getId().id()).isEqualTo(testAccount.getId().id());
              verify(accounts).save(testAccount);
            })
        .verifyComplete();
  }

  @Test
  void execute_shouldThrowAuthenticationFailedException_whenWorkflowFails() {
    // Given
    var cause = new RuntimeException("Workflow error");
    when(signupWorkflow.execute(any(Email.class), anyString())).thenReturn(Mono.error(cause));

    // When
    var resultMono = signupUsecase.execute(email, password);

    // Then
    StepVerifier.create(resultMono)
        .expectErrorMatches(
            throwable ->
                throwable instanceof AuthenticationFailedException
                    && throwable.getMessage().contains("Workflow error")
                    && throwable.getCause() == cause)
        .verify();
  }
}
