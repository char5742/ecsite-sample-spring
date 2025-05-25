package com.example.ec_2024b_back.sample.infrastructure.stepimpl;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.utils.Fast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

@Fast
class ValidateInputStepImplTest {

  private ValidateInputStepImpl validateInputStep;

  @BeforeEach
  void setUp() {
    validateInputStep = new ValidateInputStepImpl();
  }

  @Test
  void apply_shouldReturnValidatedContext_whenInputIsValid() {
    // Arrange
    var name = "Valid Sample";
    var description = "Valid Description";
    var input = new CreateSampleWorkflow.Context.Input(name, description);

    // Act
    var result = validateInputStep.apply(input);

    // Assert
    StepVerifier.create(result)
        .assertNext(
            validated -> {
              assertThat(validated.name()).isEqualTo(name);
              assertThat(validated.description()).isEqualTo(description);
            })
        .verifyComplete();
  }

  @Test
  void apply_shouldReturnValidatedContext_whenDescriptionIsNull() {
    // Arrange
    var name = "Valid Sample";
    String description = null;
    var input = new CreateSampleWorkflow.Context.Input(name, description);

    // Act
    var result = validateInputStep.apply(input);

    // Assert
    StepVerifier.create(result)
        .assertNext(
            validated -> {
              assertThat(validated.name()).isEqualTo(name);
              assertThat(validated.description()).isNull();
            })
        .verifyComplete();
  }

  @Test
  void apply_shouldThrowDomainException_whenNameIsNull() {
    // Arrange
    String name = null;
    var description = "Valid Description";
    var input = new CreateSampleWorkflow.Context.Input(name, description);

    // Act
    var result = validateInputStep.apply(input);

    // Assert
    StepVerifier.create(result)
        .expectErrorMatches(
            throwable ->
                throwable instanceof DomainException && throwable.getMessage().equals("名前は必須です"))
        .verify();
  }

  @Test
  void apply_shouldThrowDomainException_whenNameIsEmpty() {
    // Arrange
    var name = "";
    var description = "Valid Description";
    var input = new CreateSampleWorkflow.Context.Input(name, description);

    // Act
    var result = validateInputStep.apply(input);

    // Assert
    StepVerifier.create(result)
        .expectErrorMatches(
            throwable ->
                throwable instanceof DomainException && throwable.getMessage().equals("名前は必須です"))
        .verify();
  }

  @Test
  void apply_shouldThrowDomainException_whenNameIsBlank() {
    // Arrange
    var name = "   ";
    var description = "Valid Description";
    var input = new CreateSampleWorkflow.Context.Input(name, description);

    // Act
    var result = validateInputStep.apply(input);

    // Assert
    StepVerifier.create(result)
        .expectErrorMatches(
            throwable ->
                throwable instanceof DomainException && throwable.getMessage().equals("名前は必須です"))
        .verify();
  }

  @Test
  void apply_shouldThrowDomainException_whenNameExceedsMaxLength() {
    // Arrange
    var name = "a".repeat(101); // 101文字
    var description = "Valid Description";
    var input = new CreateSampleWorkflow.Context.Input(name, description);

    // Act
    var result = validateInputStep.apply(input);

    // Assert
    StepVerifier.create(result)
        .expectErrorMatches(
            throwable ->
                throwable instanceof DomainException
                    && throwable.getMessage().equals("名前は100文字以内で入力してください"))
        .verify();
  }

  @Test
  void apply_shouldReturnValidatedContext_whenNameIsExactlyMaxLength() {
    // Arrange
    var name = "a".repeat(100); // 100文字（最大長）
    var description = "Valid Description";
    var input = new CreateSampleWorkflow.Context.Input(name, description);

    // Act
    var result = validateInputStep.apply(input);

    // Assert
    StepVerifier.create(result)
        .assertNext(
            validated -> {
              assertThat(validated.name()).isEqualTo(name);
              assertThat(validated.description()).isEqualTo(description);
            })
        .verifyComplete();
  }

  @Test
  void apply_shouldThrowDomainException_whenDescriptionExceedsMaxLength() {
    // Arrange
    var name = "Valid Sample";
    var description = "a".repeat(501); // 501文字
    var input = new CreateSampleWorkflow.Context.Input(name, description);

    // Act
    var result = validateInputStep.apply(input);

    // Assert
    StepVerifier.create(result)
        .expectErrorMatches(
            throwable ->
                throwable instanceof DomainException
                    && throwable.getMessage().equals("説明は500文字以内で入力してください"))
        .verify();
  }

  @Test
  void apply_shouldReturnValidatedContext_whenDescriptionIsExactlyMaxLength() {
    // Arrange
    var name = "Valid Sample";
    var description = "a".repeat(500); // 500文字（最大長）
    var input = new CreateSampleWorkflow.Context.Input(name, description);

    // Act
    var result = validateInputStep.apply(input);

    // Assert
    StepVerifier.create(result)
        .assertNext(
            validated -> {
              assertThat(validated.name()).isEqualTo(name);
              assertThat(validated.description()).isEqualTo(description);
            })
        .verifyComplete();
  }
}
