package com.example.ec_2024b_back.sample.infrastructure.workflowimpl;

import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * サンプル作成ワークフローの実装。
 *
 * <p>このクラスは、ワークフローの具体的な実装と依存性注入の例を示します。 各ステップを順次実行し、コンテキストを通じてデータを受け渡します。
 */
@Component
@RequiredArgsConstructor
public class CreateSampleWorkflowImpl implements CreateSampleWorkflow {

  private final ValidateInputStep validateInputStep;
  private final CreateSampleStep createSampleStep;
  private final SaveSampleStep saveSampleStep;

  @Override
  public Mono<Context.Created> execute(Context.Input input) {
    return Mono.just(input)
        .flatMap(validateInputStep)
        .flatMap(createSampleStep)
        .flatMap(saveSampleStep);
  }
}
