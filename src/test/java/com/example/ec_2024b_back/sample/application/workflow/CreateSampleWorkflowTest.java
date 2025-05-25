package com.example.ec_2024b_back.sample.application.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow.CreateSampleStep;
import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow.SaveSampleStep;
import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow.ValidateInputStep;
import com.example.ec_2024b_back.sample.domain.models.Sample;
import com.example.ec_2024b_back.sample.domain.models.SampleStatus;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.share.domain.models.AuditInfo;
import com.example.ec_2024b_back.utils.Fast;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Fast
class CreateSampleWorkflowTest {

  private ValidateInputStep validateInputStep;
  private CreateSampleStep createSampleStep;
  private SaveSampleStep saveSampleStep;
  private CreateSampleWorkflow createSampleWorkflow;

  @BeforeEach
  void setUp() {
    validateInputStep = mock(ValidateInputStep.class);
    createSampleStep = mock(CreateSampleStep.class);
    saveSampleStep = mock(SaveSampleStep.class);

    // Test implementation of CreateSampleWorkflow
    createSampleWorkflow =
        new CreateSampleWorkflow() {
          @Override
          public Mono<Context.Created> execute(Context.Input input) {
            return Mono.just(input)
                .flatMap(validateInputStep)
                .flatMap(createSampleStep)
                .flatMap(saveSampleStep);
          }
        };
  }

  @Test
  void execute_shouldReturnCreatedSample_whenAllStepsSucceed() {
    // Arrange
    var name = "Test Sample";
    var description = "Test Description";
    var input = new CreateSampleWorkflow.Context.Input(name, description);
    var validated = new CreateSampleWorkflow.Context.Validated(name, description);
    var sampleId = new SampleId(UUID.randomUUID());
    var now = Instant.now();
    var auditInfo = new AuditInfo(now, now);
    var sample = new Sample(sampleId, name, description, SampleStatus.ACTIVE, auditInfo);
    var sampleCreated = new CreateSampleWorkflow.Context.SampleCreated(sample);
    var created = new CreateSampleWorkflow.Context.Created(sample);

    when(validateInputStep.apply(any(CreateSampleWorkflow.Context.Input.class)))
        .thenReturn(Mono.just(validated));
    when(createSampleStep.apply(any(CreateSampleWorkflow.Context.Validated.class)))
        .thenReturn(Mono.just(sampleCreated));
    when(saveSampleStep.apply(any(CreateSampleWorkflow.Context.SampleCreated.class)))
        .thenReturn(Mono.just(created));

    // Act
    var result = createSampleWorkflow.execute(input);

    // Assert
    StepVerifier.create(result)
        .assertNext(
            createdContext -> {
              assertThat(createdContext.sample()).isNotNull();
              assertThat(createdContext.sample().getName()).isEqualTo(name);
              assertThat(createdContext.sample().getDescription()).isEqualTo(description);
              assertThat(createdContext.sample().getStatus()).isEqualTo(SampleStatus.ACTIVE);
            })
        .verifyComplete();
  }

  @Test
  void execute_shouldPropagateError_whenValidationFails() {
    // Arrange
    var name = "";
    var description = "Test Description";
    var input = new CreateSampleWorkflow.Context.Input(name, description);
    var validationError = new CreateSampleWorkflow.InvalidInputException("名前は必須です");

    when(validateInputStep.apply(any(CreateSampleWorkflow.Context.Input.class)))
        .thenReturn(Mono.error(validationError));

    // Act
    var result = createSampleWorkflow.execute(input);

    // Assert
    StepVerifier.create(result)
        .expectError(CreateSampleWorkflow.InvalidInputException.class)
        .verify();
  }

  @Test
  void execute_shouldPropagateError_whenSampleCreationFails() {
    // Arrange
    var name = "Test Sample";
    var description = "Test Description";
    var input = new CreateSampleWorkflow.Context.Input(name, description);
    var validated = new CreateSampleWorkflow.Context.Validated(name, description);
    var creationError = new DomainException("サンプル作成に失敗しました");

    when(validateInputStep.apply(any(CreateSampleWorkflow.Context.Input.class)))
        .thenReturn(Mono.just(validated));
    when(createSampleStep.apply(any(CreateSampleWorkflow.Context.Validated.class)))
        .thenReturn(Mono.error(creationError));

    // Act
    var result = createSampleWorkflow.execute(input);

    // Assert
    StepVerifier.create(result).expectError(DomainException.class).verify();
  }

  @Test
  void execute_shouldPropagateError_whenSaveFails() {
    // Arrange
    var name = "Test Sample";
    var description = "Test Description";
    var input = new CreateSampleWorkflow.Context.Input(name, description);
    var validated = new CreateSampleWorkflow.Context.Validated(name, description);
    var sampleId = new SampleId(UUID.randomUUID());
    var now = Instant.now();
    var auditInfo = new AuditInfo(now, now);
    var sample = new Sample(sampleId, name, description, SampleStatus.ACTIVE, auditInfo);
    var sampleCreated = new CreateSampleWorkflow.Context.SampleCreated(sample);
    var saveError = new RuntimeException("データベースエラー");

    when(validateInputStep.apply(any(CreateSampleWorkflow.Context.Input.class)))
        .thenReturn(Mono.just(validated));
    when(createSampleStep.apply(any(CreateSampleWorkflow.Context.Validated.class)))
        .thenReturn(Mono.just(sampleCreated));
    when(saveSampleStep.apply(any(CreateSampleWorkflow.Context.SampleCreated.class)))
        .thenReturn(Mono.error(saveError));

    // Act
    var result = createSampleWorkflow.execute(input);

    // Assert
    StepVerifier.create(result).expectError(RuntimeException.class).verify();
  }

  @Test
  void execute_shouldHandleNullDescription() {
    // Arrange
    var name = "Test Sample";
    String description = null;
    var input = new CreateSampleWorkflow.Context.Input(name, description);
    var validated = new CreateSampleWorkflow.Context.Validated(name, description);
    var sampleId = new SampleId(UUID.randomUUID());
    var now = Instant.now();
    var auditInfo = new AuditInfo(now, now);
    var sample = new Sample(sampleId, name, description, SampleStatus.ACTIVE, auditInfo);
    var sampleCreated = new CreateSampleWorkflow.Context.SampleCreated(sample);
    var created = new CreateSampleWorkflow.Context.Created(sample);

    when(validateInputStep.apply(any(CreateSampleWorkflow.Context.Input.class)))
        .thenReturn(Mono.just(validated));
    when(createSampleStep.apply(any(CreateSampleWorkflow.Context.Validated.class)))
        .thenReturn(Mono.just(sampleCreated));
    when(saveSampleStep.apply(any(CreateSampleWorkflow.Context.SampleCreated.class)))
        .thenReturn(Mono.just(created));

    // Act
    var result = createSampleWorkflow.execute(input);

    // Assert
    StepVerifier.create(result)
        .assertNext(
            createdContext -> {
              assertThat(createdContext.sample()).isNotNull();
              assertThat(createdContext.sample().getName()).isEqualTo(name);
              assertThat(createdContext.sample().getDescription()).isNull();
            })
        .verifyComplete();
  }
}
