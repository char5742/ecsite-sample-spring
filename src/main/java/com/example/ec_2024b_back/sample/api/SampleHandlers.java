package com.example.ec_2024b_back.sample.api;

import com.example.ec_2024b_back.sample.domain.models.Sample;
import org.jspecify.annotations.Nullable;
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
   * サンプルをIDで取得します。
   *
   * @param id サンプルID
   * @return 見つかったサンプル
   */
  Mono<Sample> getSample(String id);
}
