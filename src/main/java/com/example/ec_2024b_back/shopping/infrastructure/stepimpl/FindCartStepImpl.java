package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.shopping.application.workflow.GetOrCreateCartWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.GetOrCreateCartWorkflow.FindCartStep;
import com.example.ec_2024b_back.shopping.domain.repositories.Carts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** ユーザーのカートを検索するステップの実装 */
@Component
@RequiredArgsConstructor
public class FindCartStepImpl implements FindCartStep {

  private final Carts carts;

  @Override
  public Mono<GetOrCreateCartWorkflow.Context.CartFound> apply(
      GetOrCreateCartWorkflow.Context.Input context) {
    return carts
        .findByAccountId(context.accountId())
        .map(GetOrCreateCartWorkflow.Context.CartFound::new);
  }
}
