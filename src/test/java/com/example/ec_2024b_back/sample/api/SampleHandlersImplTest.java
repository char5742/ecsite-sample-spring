package com.example.ec_2024b_back.sample.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.sample.application.usecase.CreateSampleUsecase;
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

/**
 * SampleHandlersImplのテスト。
 *
 * <p>このクラスは、APIハンドラー層の単体テスト例を示します。 モックを使用して依存性を注入し、ユースケースやリポジトリとの連携をテストします。
 */
@Fast
@ExtendWith(MockitoExtension.class)
class SampleHandlersImplTest {

  @Mock private CreateSampleUsecase createSampleUsecase;

  @Mock private Samples samples;

  @InjectMocks private SampleHandlersImpl handlers;

  private Sample sampleEntity;

  @BeforeEach
  void setUp() {
    var id = new SampleId(UUID.randomUUID());
    var now = Instant.now();
    var auditInfo = new AuditInfo(now, now);
    sampleEntity = new Sample(id, "テストサンプル", "説明", SampleStatus.DRAFT, auditInfo);
  }

  @Test
  void shouldCreateSample_whenUsecaseSucceeds() {
    when(createSampleUsecase.execute(anyString(), anyString())).thenReturn(Mono.just(sampleEntity));

    StepVerifier.create(handlers.createSample("テストサンプル", "説明"))
        .expectNext(sampleEntity)
        .verifyComplete();

    verify(createSampleUsecase).execute("テストサンプル", "説明");
  }

  @Test
  void shouldGetSample_whenSampleExists() {
    var idString = UUID.randomUUID().toString();
    when(samples.findById(any(SampleId.class))).thenReturn(Mono.just(sampleEntity));

    StepVerifier.create(handlers.getSample(idString)).expectNext(sampleEntity).verifyComplete();

    verify(samples).findById(any(SampleId.class));
  }

  @Test
  void shouldThrowError_whenSampleNotFound() {
    var idString = UUID.randomUUID().toString();
    when(samples.findById(any(SampleId.class))).thenReturn(Mono.empty());

    StepVerifier.create(handlers.getSample(idString))
        .expectErrorMatches(
            throwable ->
                throwable instanceof IllegalArgumentException
                    && throwable.getMessage().contains("サンプルが見つかりません"))
        .verify();
  }

  @Test
  void shouldThrowError_whenInvalidUUID() {
    var invalidId = "invalid-uuid";

    StepVerifier.create(handlers.getSample(invalidId))
        .expectError(IllegalArgumentException.class)
        .verify();

    verify(samples, never()).findById(any());
  }
}
