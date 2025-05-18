package com.example.ec_2024b_back.sample.application.usecase;

import com.example.ec_2024b_back.sample.domain.models.Sample;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Mono;

/**
 * サンプル作成ユースケースのインターフェース。
 *
 * <p>このインターフェースは、ユースケース層の実装例を示します。 具体的な実装はワークフローで行われます。
 */
public interface CreateSampleUsecase {
  /**
   * 新しいサンプルを作成します。
   *
   * @param name 名前
   * @param description 説明（nullable）
   * @return 作成されたサンプル
   */
  Mono<Sample> execute(String name, @Nullable String description);
}
