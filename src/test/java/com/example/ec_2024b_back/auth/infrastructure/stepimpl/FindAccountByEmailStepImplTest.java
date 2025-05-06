package com.example.ec_2024b_back.auth.infrastructure.stepimpl;

import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow;
import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.repositories.Accounts;
import com.example.ec_2024b_back.share.domain.models.Email;
import com.example.ec_2024b_back.utils.Fast;
import com.google.common.collect.ImmutableList;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Fast
@ExtendWith(MockitoExtension.class)
class FindAccountByEmailStepImplTest {

  @Mock private Accounts accounts;

  @InjectMocks private FindAccountByEmailStepImpl findAccountByEmailStep;

  @Test
  void apply_shouldReturnFoundedContext_whenAccountExists() {
    // Arrange
    var email = new Email("test@example.com");
    var password = "password";
    var input = new LoginWorkflow.Context.Input(email, password);
    var uuid = UUID.randomUUID();
    var account = Account.reconstruct(new AccountId(uuid), ImmutableList.of());

    when(accounts.findByEmail(email)).thenReturn(Mono.just(account));

    // Act
    var result = findAccountByEmailStep.apply(input);

    // Assert
    StepVerifier.create(result)
        .assertNext(
            founded -> {
              org.assertj.core.api.Assertions.assertThat(founded.account()).isEqualTo(account);
              org.assertj.core.api.Assertions.assertThat(founded.rawPassword()).isEqualTo(password);
            })
        .verifyComplete();
  }

  @Test
  void apply_shouldReturnUserNotFoundException_whenAccountNotFound() {
    // Arrange
    var email = new Email("nonexistent@example.com");
    var password = "password";
    var input = new LoginWorkflow.Context.Input(email, password);

    when(accounts.findByEmail(email)).thenReturn(Mono.empty());

    // Act
    var result = findAccountByEmailStep.apply(input);

    // Assert
    StepVerifier.create(result).expectError(LoginWorkflow.UserNotFoundException.class).verify();
  }

  @Test
  void apply_shouldReturnUserNotFoundException_whenRepositoryError() {
    // Arrange
    var email = new Email("error@example.com");
    var password = "password";
    var input = new LoginWorkflow.Context.Input(email, password);
    var error = new RuntimeException("Repository error");

    when(accounts.findByEmail(email)).thenReturn(Mono.error(error));

    // Act
    var result = findAccountByEmailStep.apply(input);

    // Assert
    StepVerifier.create(result).expectError(LoginWorkflow.UserNotFoundException.class).verify();
  }
}
