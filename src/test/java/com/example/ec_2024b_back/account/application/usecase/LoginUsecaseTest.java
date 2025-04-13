package com.example.ec_2024b_back.account.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ec_2024b_back.account.domain.workflow.LoginWorkflow;
import com.example.ec_2024b_back.model.LoginDto;
import com.example.ec_2024b_back.utils.Fast;

import io.vavr.control.Try;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@Fast
class LoginUsecaseTest {

  @Mock private LoginWorkflow loginWorkflow;

  @InjectMocks private LoginUsecase loginUsecase;

  private final LoginDto loginDto = new LoginDto().email("test@example.com").password("password");
  private final String expectedToken = "jwt.token";

  @Test
  void execute_shouldReturnSuccessDto_whenWorkflowSucceeds() {
    // Arrange
    when(loginWorkflow.execute(anyString(), anyString()))
        .thenReturn(Mono.just(Try.success(expectedToken)));

    // Act
    var resultMono = loginUsecase.execute(loginDto);

    // Assert
    StepVerifier.create(resultMono)
        .assertNext(
            successDto -> {
              assertThat(successDto).isNotNull();
              assertThat(successDto.token()).isEqualTo(expectedToken);
            })
        .verifyComplete();
  }

  @Test
  void execute_shouldReturnError_whenWorkflowFails() {
    // Arrange
    var workflowException = new LoginWorkflow.UserNotFoundException(loginDto.getEmail());
    when(loginWorkflow.execute(anyString(), anyString()))
        .thenReturn(Mono.just(Try.failure(workflowException)));

    // Act
    var resultMono = loginUsecase.execute(loginDto);

    // Assert
    StepVerifier.create(resultMono)
        .expectErrorSatisfies(
            error -> {
              assertThat(error).isInstanceOf(LoginUsecase.AuthenticationFailedException.class);
              assertThat(error.getCause()).isEqualTo(workflowException);
            })
        .verify();
  }

  @Test
  void execute_shouldReturnError_whenWorkflowReturnsErrorMono() {
    // Arrange
    var workflowException = new RuntimeException("Workflow Mono error");
    when(loginWorkflow.execute(anyString(), anyString())).thenReturn(Mono.error(workflowException));

    // Act
    var resultMono = loginUsecase.execute(loginDto);

    // Assert
    StepVerifier.create(resultMono)
        .expectErrorSatisfies(
            error -> {
              assertThat(error).isEqualTo(workflowException);
            })
        .verify();
  }
}
