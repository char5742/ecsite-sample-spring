package com.example.ec_2024b_back.sample.infrastructure.stepimpl;

import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow;
import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow.SaveSampleStep;
import com.example.ec_2024b_back.sample.domain.repositories.Samples;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * サンプル保存ステップの実装。
 *
 * <p>このクラスは、リポジトリを使用したエンティティ永続化の例を示します。
 */
@Component
@RequiredArgsConstructor
public class SaveSampleStepImpl implements SaveSampleStep {
  private final Samples samples;

  @Override
  public Mono<CreateSampleWorkflow.Context.Created> apply(
      CreateSampleWorkflow.Context.SampleCreated sampleCreated) {
    return samples.save(sampleCreated.sample()).map(CreateSampleWorkflow.Context.Created::new);
  }
}
