package com.example.ec_2024b_back.sample.application.workflow;

import com.example.ec_2024b_back.sample.application.usecase.CreateSampleUsecase;
import com.example.ec_2024b_back.sample.domain.models.Sample;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Mono;

/**
 * サンプル作成ワークフロー。
 *
 * <p>このクラスは、複数のステップで構成されるワークフローパターンの実装例を示します。 各ステップは独立したインターフェースとして定義され、テスト容易性を高めています。
 */
@Getter
public abstract class CreateSampleWorkflow implements CreateSampleUsecase {
  private final ValidateInputStep validateInputStep;
  private final CreateSampleStep createSampleStep;
  private final SaveSampleStep saveSampleStep;

  protected CreateSampleWorkflow(
      ValidateInputStep validateInputStep,
      CreateSampleStep createSampleStep,
      SaveSampleStep saveSampleStep) {
    this.validateInputStep = validateInputStep;
    this.createSampleStep = createSampleStep;
    this.saveSampleStep = saveSampleStep;
  }

  @Override
  public Mono<Sample> execute(String name, @Nullable String description) {
    return validateInputStep
        .execute(name, description)
        .then(createSampleStep.execute(name, description))
        .flatMap(saveSampleStep::execute);
  }

  /** 入力検証ステップ。 */
  public interface ValidateInputStep {
    /**
     * 入力値を検証します。
     *
     * @param name 名前
     * @param description 説明
     * @return 検証結果
     */
    Mono<Void> execute(String name, @Nullable String description);
  }

  /** サンプル作成ステップ。 */
  public interface CreateSampleStep {
    /**
     * サンプルを作成します。
     *
     * @param name 名前
     * @param description 説明
     * @return 作成されたサンプル
     */
    Mono<Sample> execute(String name, @Nullable String description);
  }

  /** サンプル保存ステップ。 */
  public interface SaveSampleStep {
    /**
     * サンプルを保存します。
     *
     * @param sample サンプル
     * @return 保存されたサンプル
     */
    Mono<Sample> execute(Sample sample);
  }
}
