package com.example.ec_2024b_back.sample.application.workflow;

import com.example.ec_2024b_back.sample.domain.models.Sample;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Mono;

/**
 * サンプル作成ワークフローのインターフェース。
 *
 * <p>このインターフェースは、複数のステップで構成されるワークフローパターンの実装例を示します。 各ステップは独立したインターフェースとして定義され、テスト容易性を高めています。
 */
public interface CreateSampleWorkflow {

  /** 入力検証ステップ。 */
  @FunctionalInterface
  interface ValidateInputStep extends Function<Context.Input, Mono<Context.Validated>> {}

  /** サンプル作成ステップ。 */
  @FunctionalInterface
  interface CreateSampleStep extends Function<Context.Validated, Mono<Context.SampleCreated>> {}

  /** サンプル保存ステップ。 */
  @FunctionalInterface
  interface SaveSampleStep extends Function<Context.SampleCreated, Mono<Context.Created>> {}

  /**
   * ワークフローを実行します。
   *
   * @param input 入力コンテキスト
   * @return 作成されたサンプルを含むコンテキスト
   */
  Mono<Context.Created> execute(Context.Input input);

  /** ワークフローのコンテキストを定義するsealed interface。 */
  sealed interface Context
      permits Context.Input, Context.Validated, Context.SampleCreated, Context.Created {

    /**
     * 入力コンテキスト。
     *
     * @param name 名前
     * @param description 説明（nullable）
     */
    record Input(String name, @Nullable String description) implements Context {}

    /**
     * 検証済みコンテキスト。
     *
     * @param name 検証済みの名前
     * @param description 検証済みの説明（nullable）
     */
    record Validated(String name, @Nullable String description) implements Context {}

    /**
     * サンプル作成済みコンテキスト。
     *
     * @param sample 作成されたサンプル（未保存）
     */
    record SampleCreated(Sample sample) implements Context {}

    /**
     * 作成完了コンテキスト。
     *
     * @param sample 保存されたサンプル
     */
    record Created(Sample sample) implements Context {}
  }

  /** 入力値が不正な場合のカスタム例外。 */
  class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
      super(message);
    }
  }
}
