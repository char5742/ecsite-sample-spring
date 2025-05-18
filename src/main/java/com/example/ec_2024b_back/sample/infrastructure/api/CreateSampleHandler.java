package com.example.ec_2024b_back.sample.infrastructure.api;

import com.example.ec_2024b_back.sample.api.SampleHandlers;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * サンプル作成APIハンドラー。
 *
 * <p>このクラスは、Spring WebFluxのfunctional endpointでの実装例を示します。 リクエスト/レスポンスの処理を行います。
 */
@Component
@RequiredArgsConstructor
public class CreateSampleHandler {
  private final SampleHandlers sampleHandlers;

  /**
   * サンプル作成リクエストを処理します。
   *
   * @param request リクエスト
   * @return レスポンス
   */
  public Mono<ServerResponse> handle(ServerRequest request) {
    return request
        .bodyToMono(CreateSampleRequest.class)
        .flatMap(req -> sampleHandlers.createSample(req.name(), req.description()))
        .map(sample -> new CreateSampleResponse(sample.getId().toString(), sample.getName()))
        .flatMap(response -> ServerResponse.ok().bodyValue(response))
        .onErrorResume(
            IllegalArgumentException.class,
            e -> ServerResponse.badRequest().bodyValue(new ErrorResponse(e.getMessage())));
  }

  /** サンプル作成リクエスト。 */
  public record CreateSampleRequest(String name, @Nullable String description) {}

  /** サンプル作成レスポンス。 */
  public record CreateSampleResponse(String id, String name) {}

  /** エラーレスポンス。 */
  public record ErrorResponse(String message) {}
}
