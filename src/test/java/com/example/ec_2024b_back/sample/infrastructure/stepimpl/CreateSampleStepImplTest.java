package com.example.ec_2024b_back.sample.infrastructure.stepimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow;
import com.example.ec_2024b_back.sample.domain.models.Sample;
import com.example.ec_2024b_back.sample.domain.models.SampleStatus;
import com.example.ec_2024b_back.sample.domain.services.SampleFactory;
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
import reactor.test.StepVerifier;

@Fast
@ExtendWith(MockitoExtension.class)
class CreateSampleStepImplTest {

  @Mock private SampleFactory sampleFactory;

  @InjectMocks private CreateSampleStepImpl createSampleStep;

  @BeforeEach
  void setUp() {
    // No additional setup needed
  }

  @Test
  void apply_shouldReturnSampleCreatedContext_whenFactoryCreatesSample() {
    // Arrange
    var name = "Test Sample";
    var description = "Test Description";
    var validated = new CreateSampleWorkflow.Context.Validated(name, description);
    var sampleId = new SampleId(UUID.randomUUID());
    var now = Instant.now();
    var auditInfo = new AuditInfo(now, now);
    var sample = new Sample(sampleId, name, description, SampleStatus.ACTIVE, auditInfo);

    when(sampleFactory.create(name, description)).thenReturn(sample);

    // Act
    var result = createSampleStep.apply(validated);

    // Assert
    StepVerifier.create(result)
        .assertNext(
            sampleCreated -> {
              assertThat(sampleCreated.sample()).isNotNull();
              assertThat(sampleCreated.sample().getName()).isEqualTo(name);
              assertThat(sampleCreated.sample().getDescription()).isEqualTo(description);
              assertThat(sampleCreated.sample().getStatus()).isEqualTo(SampleStatus.ACTIVE);
            })
        .verifyComplete();
  }

  @Test
  void apply_shouldReturnSampleCreatedContext_whenDescriptionIsNull() {
    // Arrange
    var name = "Test Sample";
    String description = null;
    var validated = new CreateSampleWorkflow.Context.Validated(name, description);
    var sampleId = new SampleId(UUID.randomUUID());
    var now = Instant.now();
    var auditInfo = new AuditInfo(now, now);
    var sample = new Sample(sampleId, name, description, SampleStatus.ACTIVE, auditInfo);

    when(sampleFactory.create(name, description)).thenReturn(sample);

    // Act
    var result = createSampleStep.apply(validated);

    // Assert
    StepVerifier.create(result)
        .assertNext(
            sampleCreated -> {
              assertThat(sampleCreated.sample()).isNotNull();
              assertThat(sampleCreated.sample().getName()).isEqualTo(name);
              assertThat(sampleCreated.sample().getDescription()).isNull();
            })
        .verifyComplete();
  }

  @Test
  void apply_shouldPropagateException_whenFactoryThrowsException() {
    // Arrange
    var name = "Test Sample";
    var description = "Test Description";
    var validated = new CreateSampleWorkflow.Context.Validated(name, description);
    var exception = new RuntimeException("Factory error");

    when(sampleFactory.create(name, description)).thenThrow(exception);

    // Act
    var result = createSampleStep.apply(validated);

    // Assert
    StepVerifier.create(result).expectError(RuntimeException.class).verify();
  }
}
