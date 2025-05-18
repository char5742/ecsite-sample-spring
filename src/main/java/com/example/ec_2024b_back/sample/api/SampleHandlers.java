package com.example.ec_2024b_back.sample.api;

import com.example.ec_2024b_back.sample.domain.models.Sample;
import org.jspecify.annotations.Nullable;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * サンプルAPIのハンドラーインターフェース。
 *
 * <p>このインターフェースは、APIレイヤーの抽象化例を示します。 具体的な実装はインフラストラクチャ層で行われます。
 */
public interface SampleHandlers {
  /**
   * サンプルを作成します。
   *
   * @param name 名前
   * @param description 説明
   * @return 作成されたサンプル
   */
  Mono<Sample> createSample(String name, @Nullable String description);

  /**
   * サンプルを作成します（HTTPリクエスト用）。
   *
   * @param request HTTPリクエスト
   * @return HTTPレスポンス
   */
  Mono<ServerResponse> createSample(ServerRequest request);

  /**
   * サンプルをIDで取得します。
   *
   * @param id サンプルID
   * @return 見つかったサンプル
   */
  Mono<Sample> getSample(String id);

  /**
   * サンプルをIDで取得します（HTTPリクエスト用）。
   *
   * @param request HTTPリクエスト
   * @return HTTPレスポンス
   */
  Mono<ServerResponse> getSample(ServerRequest request);
}
