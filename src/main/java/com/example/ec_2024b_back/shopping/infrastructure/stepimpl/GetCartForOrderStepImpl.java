package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.shopping.application.workflow.CreateOrderFromCartWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.CreateOrderFromCartWorkflow.GetCartStep;
import com.example.ec_2024b_back.shopping.domain.repositories.Carts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 注文作成のためのカート取得ステップの実装 */
@Component
@RequiredArgsConstructor
public class GetCartForOrderStepImpl implements GetCartStep {

  private final Carts carts;

  @Override
  public Mono<CreateOrderFromCartWorkflow.Context.CartFound> apply(
      CreateOrderFromCartWorkflow.Context.Input context) {
    return carts
        .findByAccountId(context.accountId())
        .switchIfEmpty(
            Mono.error(
                new CreateOrderFromCartWorkflow.CartNotFoundException(
                    "アカウントID: " + context.accountId() + " のカートが見つかりません")))
        .map(
            cart ->
                new CreateOrderFromCartWorkflow.Context.CartFound(cart, context.shippingAddress()));
  }
}
