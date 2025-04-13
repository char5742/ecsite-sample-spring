package com.example.ec_2024b_back.account.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.account.application.usecase.LoginUsecase.AuthenticationFailedException;
import com.example.ec_2024b_back.account.domain.workflow.LoginWorkflow;
import com.example.ec_2024b_back.model.LoginDto;
import com.example.ec_2024b_back.utils.Fast;
import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Fast
@ExtendWith(MockitoExtension.class)
class LoginUsecaseTest {

  @Mock private LoginWorkflow loginWorkflow;

  @InjectMocks private LoginUsecase loginUsecase;

  private LoginDto loginDto;

  @BeforeEach
  void setUp() {
    loginDto = new LoginDto();
    loginDto.setEmail("test@example.com");
    loginDto.setPassword("password");
  }

  @Test
  void execute_shouldReturnSuccessDto_whenWorkflowSucceeds() {
    var expectedToken = "dummy-jwt-token";

    when(loginWorkflow.execute(anyString(), anyString()))
        .thenReturn(Mono.just(Try.success(expectedToken)));

    var resultMono = loginUsecase.execute(loginDto);

    StepVerifier.create(resultMono)
        .assertNext(
            successDto -> {
              assertThat(successDto).isNotNull();
              assertThat(successDto.token()).isEqualTo(expectedToken);
            })
        .verifyComplete();
  }

  @Test
  void execute_shouldThrowAuthenticationFailedException_whenWorkflowFails() {
    var cause = new RuntimeException("Workflow error");
    when(loginWorkflow.execute(anyString(), anyString())).thenReturn(Mono.just(Try.failure(cause)));

    var resultMono = loginUsecase.execute(loginDto);

    StepVerifier.create(resultMono)
        .expectErrorMatches(
            throwable ->
                throwable instanceof AuthenticationFailedException
                    && throwable.getMessage().contains("Workflow error")
                    && throwable.getCause() == cause)
        .verify();
  }
}
