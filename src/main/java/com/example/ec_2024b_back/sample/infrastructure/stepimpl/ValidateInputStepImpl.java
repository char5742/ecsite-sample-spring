package com.example.ec_2024b_back.sample.infrastructure.stepimpl;

import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow.ValidateInputStep;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
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
    return Mono.fromCallable(
            () -> {
              validateName(name);
              validateDescription(description);
              return null;
            })
        .then();
  }

  /**
   * 名前の検証を行います。
   *
   * @param name 名前
   * @throws DomainException 検証に失敗した場合
   */
  private static void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new DomainException("名前は必須です");
    }

    if (name.length() > 100) {
      throw new DomainException("名前は100文字以内で入力してください");
    }

    // 禁止文字のチェック
    if (name.contains("<") || name.contains(">")) {
      throw new DomainException("名前に使用できない文字が含まれています");
    }
  }

  /**
   * 説明の検証を行います。
   *
   * @param description 説明
   * @throws DomainException 検証に失敗した場合
   */
  private static void validateDescription(@Nullable String description) {
    if (description == null) {
      return; // nullは許可
    }

    if (description.length() > 500) {
      throw new DomainException("説明は500文字以内で入力してください");
    }
  }
}
