package com.example.ec_2024b_back.sample.api;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.sample.application.usecase.CreateSampleUsecase;
import com.example.ec_2024b_back.sample.domain.models.Sample;
import com.example.ec_2024b_back.sample.domain.repositories.Samples;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * サンプルAPIハンドラーの実装。
 *
 * <p>このクラスは、APIハンドラーの実装例を示します。 ユースケースやリポジトリを組み合わせて、APIの処理を実装します。
 */
@Component
@RequiredArgsConstructor
public class SampleHandlersImpl implements SampleHandlers {
  private final CreateSampleUsecase createSampleUsecase;
  private final Samples samples;

  @Override
  public Mono<Sample> createSample(String name, @Nullable String description) {
    return createSampleUsecase.execute(name, description);
  }

  @Override
  public Mono<Sample> getSample(String id) {
    return Mono.fromCallable(() -> UUID.fromString(id))
        .map(SampleId::new)
        .flatMap(samples::findById)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("サンプルが見つかりません: " + id)));
  }
}
