package com.example.ec_2024b_back.sample.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow;
import com.example.ec_2024b_back.sample.domain.models.Sample;
import com.example.ec_2024b_back.sample.domain.models.SampleStatus;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.share.domain.models.AuditInfo;
import com.example.ec_2024b_back.utils.Fast;
import java.time.Instant;
import java.util.UUID;
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
class CreateSampleUsecaseTest {

  @Mock private CreateSampleWorkflow createSampleWorkflow;

  @InjectMocks private CreateSampleUsecase createSampleUsecase;

  private Sample sampleEntity;

  @BeforeEach
  void setUp() {
    var sampleId = new SampleId(UUID.randomUUID());
    var now = Instant.now();
    var auditInfo = new AuditInfo(now, now);
    sampleEntity =
        new Sample(sampleId, "Test Sample", "Test Description", SampleStatus.ACTIVE, auditInfo);
  }

  @Test
  void execute_shouldReturnSample_whenWorkflowSucceeds() {
    // Arrange
    var name = "Test Sample";
    var description = "Test Description";
    var createdContext = new CreateSampleWorkflow.Context.Created(sampleEntity);

    when(createSampleWorkflow.execute(any(CreateSampleWorkflow.Context.Input.class)))
        .thenReturn(Mono.just(createdContext));

    // Act
    var resultMono = createSampleUsecase.execute(name, description);

    // Assert
    StepVerifier.create(resultMono)
        .assertNext(
            sample -> {
              assertThat(sample).isNotNull();
              assertThat(sample.getName()).isEqualTo(name);
              assertThat(sample.getDescription()).isEqualTo(description);
              assertThat(sample.getStatus()).isEqualTo(SampleStatus.ACTIVE);
            })
        .verifyComplete();
  }

  @Test
  void execute_shouldReturnSample_whenDescriptionIsNull() {
    // Arrange
    var name = "Test Sample";
    String description = null;
    var sampleId = new SampleId(UUID.randomUUID());
    var now = Instant.now();
    var auditInfo = new AuditInfo(now, now);
    var sampleWithoutDescription =
        new Sample(sampleId, name, description, SampleStatus.ACTIVE, auditInfo);
    var createdContext = new CreateSampleWorkflow.Context.Created(sampleWithoutDescription);

    when(createSampleWorkflow.execute(any(CreateSampleWorkflow.Context.Input.class)))
        .thenReturn(Mono.just(createdContext));

    // Act
    var resultMono = createSampleUsecase.execute(name, description);

    // Assert
    StepVerifier.create(resultMono)
        .assertNext(
            sample -> {
              assertThat(sample).isNotNull();
              assertThat(sample.getName()).isEqualTo(name);
              assertThat(sample.getDescription()).isNull();
            })
        .verifyComplete();
  }

  @Test
  void execute_shouldThrowSampleCreationFailedException_whenDomainExceptionOccurs() {
    // Arrange
    var name = "Invalid Sample";
    var description = "Description";
    var domainException = new DomainException("ドメインエラーが発生しました");

    when(createSampleWorkflow.execute(any(CreateSampleWorkflow.Context.Input.class)))
        .thenReturn(Mono.error(domainException));

    // Act
    var resultMono = createSampleUsecase.execute(name, description);

    // Assert
    StepVerifier.create(resultMono)
        .expectErrorMatches(
            throwable ->
                throwable instanceof CreateSampleUsecase.SampleCreationFailedException
                    && throwable.getMessage().contains("サンプルの作成に失敗しました")
                    && throwable.getCause() == domainException)
        .verify();
  }

  @Test
  void execute_shouldPropagateOtherExceptions_whenNonDomainExceptionOccurs() {
    // Arrange
    var name = "Test Sample";
    var description = "Description";
    var runtimeException = new RuntimeException("予期しないエラー");

    when(createSampleWorkflow.execute(any(CreateSampleWorkflow.Context.Input.class)))
        .thenReturn(Mono.error(runtimeException));

    // Act
    var resultMono = createSampleUsecase.execute(name, description);

    // Assert
    StepVerifier.create(resultMono)
        .expectErrorMatches(
            throwable ->
                throwable instanceof RuntimeException && throwable.getMessage().equals("予期しないエラー"))
        .verify();
  }
}
