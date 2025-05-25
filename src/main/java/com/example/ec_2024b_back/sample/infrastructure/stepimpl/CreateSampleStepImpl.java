package com.example.ec_2024b_back.sample.infrastructure.stepimpl;

import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow;
import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow.CreateSampleStep;
import com.example.ec_2024b_back.sample.domain.services.SampleFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * サンプル作成ステップの実装。
 *
 * <p>このクラスは、ファクトリーを使用したエンティティ作成の例を示します。
 */
@Component
@RequiredArgsConstructor
public class CreateSampleStepImpl implements CreateSampleStep {
  private final SampleFactory sampleFactory;

  @Override
  public Mono<CreateSampleWorkflow.Context.SampleCreated> apply(
      CreateSampleWorkflow.Context.Validated validated) {
    return Mono.fromCallable(() -> sampleFactory.create(validated.name(), validated.description()))
        .map(CreateSampleWorkflow.Context.SampleCreated::new);
  }
}
