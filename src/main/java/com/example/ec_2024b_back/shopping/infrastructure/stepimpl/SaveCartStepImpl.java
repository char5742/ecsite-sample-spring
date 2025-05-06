package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.shopping.application.workflow.AddItemToCartWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.AddItemToCartWorkflow.SaveCartStep;
import com.example.ec_2024b_back.shopping.domain.repositories.Carts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** カートを保存するステップの実装 */
@Component
@RequiredArgsConstructor
public class SaveCartStepImpl implements SaveCartStep {

  private final Carts carts;

  @Override
  public Mono<AddItemToCartWorkflow.Context.Complete> apply(
      AddItemToCartWorkflow.Context.Added context) {
    return carts.save(context.cart()).map(AddItemToCartWorkflow.Context.Complete::new);
  }
}
