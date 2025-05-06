package com.example.ec_2024b_back.shopping.infrastructure.workflowimpl;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.application.workflow.GetOrCreateCartWorkflow;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** カート取得または作成ワークフローの実装 */
@Component
@RequiredArgsConstructor
public class GetOrCreateCartWorkflowImpl implements GetOrCreateCartWorkflow {

  private final FindCartStep findCartStep;
  private final CreateCartStep createCartStep;

  @Override
  public Mono<Cart> execute(AccountId accountId) {
    var input = new Context.Input(accountId);

    return findCartStep
        .apply(input)
        .map(Context.CartFound::cart)
        .switchIfEmpty(createCartStep.apply(input).map(Context.Created::cart));
  }
}
