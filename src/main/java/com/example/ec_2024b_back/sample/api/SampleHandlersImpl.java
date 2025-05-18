package com.example.ec_2024b_back.sample.api;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.sample.application.usecase.CreateSampleUsecase;
import com.example.ec_2024b_back.sample.domain.models.Sample;
import com.example.ec_2024b_back.sample.domain.repositories.Samples;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
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
  public Mono<ServerResponse> createSample(ServerRequest request) {
    return request
        .bodyToMono(CreateSampleRequest.class)
        .flatMap(req -> createSample(req.name(), req.description()))
        .map(sample -> new CreateSampleResponse(sample.getId().toString(), sample.getName()))
        .flatMap(response -> ServerResponse.ok().bodyValue(response))
        .onErrorResume(
            IllegalArgumentException.class,
            e ->
                ServerResponse.badRequest()
                    .bodyValue(
                        new ErrorResponse(
                            e.getMessage() != null ? e.getMessage() : "Invalid request")));
  }

  @Override
  public Mono<Sample> getSample(String id) {
    return Mono.fromCallable(() -> UUID.fromString(id))
        .map(SampleId::new)
        .flatMap(samples::findById)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("サンプルが見つかりません: " + id)));
  }

  @Override
  public Mono<ServerResponse> getSample(ServerRequest request) {
    String id = request.pathVariable("id");
    return getSample(id)
        .map(sample -> new GetSampleResponse(sample.getId().toString(), sample.getName()))
        .flatMap(response -> ServerResponse.ok().bodyValue(response))
        .onErrorResume(IllegalArgumentException.class, e -> ServerResponse.notFound().build());
  }

  /** サンプル作成リクエスト。 */
  public record CreateSampleRequest(String name, @Nullable String description) {}

  /** サンプル作成レスポンス。 */
  public record CreateSampleResponse(String id, String name) {}

  /** サンプル取得レスポンス。 */
  public record GetSampleResponse(String id, String name) {}

  /** エラーレスポンス。 */
  public record ErrorResponse(String message) {}
}
