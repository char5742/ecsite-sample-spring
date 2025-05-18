package com.example.ec_2024b_back.sample.domain.repositories;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.sample.domain.models.Sample;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * サンプルエンティティのリポジトリインターフェース。
 *
 * <p>このインターフェースは、ドメイン層で定義されるリポジトリパターンの例を示します。 実装はインフラストラクチャ層で行われます。
 */
public interface Samples {
  /**
   * サンプルをIDで検索します。
   *
   * @param id サンプルID
   * @return 見つかったサンプル、または空のMono
   */
  Mono<Sample> findById(SampleId id);

  /**
   * サンプルを名前で検索します。
   *
   * @param name 名前
   * @return 見つかったサンプルのFlux
   */
  Flux<Sample> findByName(String name);

  /**
   * サンプルを保存します。
   *
   * @param sample 保存するサンプル
   * @return 保存されたサンプル
   */
  Mono<Sample> save(Sample sample);

  /**
   * サンプルを削除します。
   *
   * @param id 削除するサンプルのID
   * @return 完了シグナル
   */
  Mono<Void> deleteById(SampleId id);
}
