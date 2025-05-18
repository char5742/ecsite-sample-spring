package com.example.ec_2024b_back.sample.infrastructure.stepimpl;

import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow.ValidateInputStep;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 入力検証ステップの実装。
 *
 * <p>このクラスは、ワークフローのステップ実装例を示します。 ビジネスルールに基づいた検証ロジックを実装します。
 */
@Component
public class ValidateInputStepImpl implements ValidateInputStep {

  @Override
  public Mono<Void> execute(String name, @Nullable String description) {
    return Mono.fromRunnable(
        () -> {
          if (name.isBlank()) {
            throw new IllegalArgumentException("名前は必須です");
          }

          if (name.length() > 100) {
            throw new IllegalArgumentException("名前は100文字以内で入力してください");
          }

          if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("説明は500文字以内で入力してください");
          }
        });
  }
}
