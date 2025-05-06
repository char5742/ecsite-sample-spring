package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.shopping.application.workflow.GetOrCreateCartWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.GetOrCreateCartWorkflow.CreateCartStep;
import com.example.ec_2024b_back.shopping.domain.repositories.Carts;
import com.example.ec_2024b_back.shopping.domain.services.CartFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 新しいカートを作成するステップの実装 */
@Component
@RequiredArgsConstructor
public class CreateCartStepImpl implements CreateCartStep {

  private final CartFactory cartFactory;
  private final Carts carts;

  @Override
  public Mono<GetOrCreateCartWorkflow.Context.Created> apply(
      GetOrCreateCartWorkflow.Context.Input context) {
    var cart = cartFactory.createEmptyCart(context.accountId());
    return carts.save(cart).map(GetOrCreateCartWorkflow.Context.Created::new);
  }
}
