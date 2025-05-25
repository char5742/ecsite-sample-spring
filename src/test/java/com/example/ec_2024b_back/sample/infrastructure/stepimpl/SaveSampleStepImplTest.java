package com.example.ec_2024b_back.sample.infrastructure.stepimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow;
import com.example.ec_2024b_back.sample.domain.models.Sample;
import com.example.ec_2024b_back.sample.domain.models.SampleStatus;
import com.example.ec_2024b_back.sample.domain.repositories.Samples;
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
class SaveSampleStepImplTest {

  @Mock private Samples samples;

  @InjectMocks private SaveSampleStepImpl saveSampleStep;

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
  void apply_shouldReturnCreatedContext_whenSaveSucceeds() {
    // Arrange
    var sampleCreated = new CreateSampleWorkflow.Context.SampleCreated(sampleEntity);
    var savedSample = sampleEntity; // 通常は保存後に更新されたタイムスタンプ等を持つ

    when(samples.save(any(Sample.class))).thenReturn(Mono.just(savedSample));

    // Act
    var result = saveSampleStep.apply(sampleCreated);

    // Assert
    StepVerifier.create(result)
        .assertNext(
            created -> {
              assertThat(created.sample()).isNotNull();
              assertThat(created.sample().getId()).isEqualTo(sampleEntity.getId());
              assertThat(created.sample().getName()).isEqualTo(sampleEntity.getName());
              assertThat(created.sample().getDescription())
                  .isEqualTo(sampleEntity.getDescription());
            })
        .verifyComplete();
  }

  @Test
  void apply_shouldPropagateError_whenSaveFails() {
    // Arrange
    var sampleCreated = new CreateSampleWorkflow.Context.SampleCreated(sampleEntity);
    var saveError = new RuntimeException("データベースエラー");

    when(samples.save(any(Sample.class))).thenReturn(Mono.error(saveError));

    // Act
    var result = saveSampleStep.apply(sampleCreated);

    // Assert
    StepVerifier.create(result).expectError(RuntimeException.class).verify();
  }

  @Test
  void apply_shouldHandleSampleWithNullDescription() {
    // Arrange
    var sampleId = new SampleId(UUID.randomUUID());
    var now = Instant.now();
    var auditInfo = new AuditInfo(now, now);
    var sampleWithNullDescription =
        new Sample(sampleId, "Test Sample", null, SampleStatus.ACTIVE, auditInfo);
    var sampleCreated = new CreateSampleWorkflow.Context.SampleCreated(sampleWithNullDescription);

    when(samples.save(any(Sample.class))).thenReturn(Mono.just(sampleWithNullDescription));

    // Act
    var result = saveSampleStep.apply(sampleCreated);

    // Assert
    StepVerifier.create(result)
        .assertNext(
            created -> {
              assertThat(created.sample()).isNotNull();
              assertThat(created.sample().getDescription()).isNull();
            })
        .verifyComplete();
  }
}
