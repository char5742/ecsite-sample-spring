package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.shopping.application.workflow.AddItemToCartWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.AddItemToCartWorkflow.GetCartStep;
import com.example.ec_2024b_back.shopping.domain.repositories.Carts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** カートを取得するステップの実装 */
@Component
@RequiredArgsConstructor
public class GetCartForAddItemStepImpl implements GetCartStep {

  private final Carts carts;

  @Override
  public Mono<AddItemToCartWorkflow.Context.CartFound> apply(
      AddItemToCartWorkflow.Context.Input context) {
    return carts
        .findByAccountId(context.accountId())
        .switchIfEmpty(
            Mono.error(
                new AddItemToCartWorkflow.CartNotFoundException(
                    "アカウントID: " + context.accountId() + " のカートが見つかりません")))
        .map(
            cart ->
                new AddItemToCartWorkflow.Context.CartFound(
                    cart,
                    context.productId(),
                    context.productName(),
                    context.unitPrice(),
                    context.quantity()));
  }
}
