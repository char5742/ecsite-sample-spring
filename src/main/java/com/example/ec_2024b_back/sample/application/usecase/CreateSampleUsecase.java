package com.example.ec_2024b_back.sample.application.usecase;

import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow;
import com.example.ec_2024b_back.sample.domain.models.Sample;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * サンプル作成ユースケースを実装するクラス。
 *
 * <p>このクラスは、アプリケーション層のサービスとして、ワークフローの実行と 横断的関心事（イベント発行、エラー変換など）を処理します。
 */
@Service
@RequiredArgsConstructor
public class CreateSampleUsecase {

  private final CreateSampleWorkflow createSampleWorkflow;

  /**
   * 新しいサンプルを作成します。
   *
   * @param name 名前
   * @param description 説明（nullable）
   * @return 作成されたサンプル
   */
  public Mono<Sample> execute(String name, @Nullable String description) {
    var context = new CreateSampleWorkflow.Context.Input(name, description);

    return createSampleWorkflow
        .execute(context)
        .onErrorMap(this::mapToApplicationException)
        .map(CreateSampleWorkflow.Context.Created::sample);
  }

  /**
   * ドメイン例外をアプリケーション層の例外に変換します。
   *
   * @param throwable 発生した例外
   * @return アプリケーション層の例外
   */
  private Throwable mapToApplicationException(Throwable throwable) {
    if (throwable instanceof DomainException) {
      return new SampleCreationFailedException(throwable);
    }
    return throwable;
  }

  /** サンプル作成失敗を表すカスタム例外。 */
  public static class SampleCreationFailedException extends RuntimeException {
    public SampleCreationFailedException(Throwable cause) {
      super("サンプルの作成に失敗しました: " + cause.getMessage(), cause);
    }
  }
}
