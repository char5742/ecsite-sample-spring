package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.shopping.application.workflow.AddItemToCartWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.AddItemToCartWorkflow.AddItemStep;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** カートに商品を追加するステップの実装 */
@Component
public class AddItemStepImpl implements AddItemStep {

  private final Clock clock = Clock.system(ZoneId.systemDefault());

  @Override
  public Mono<AddItemToCartWorkflow.Context.Added> apply(
      AddItemToCartWorkflow.Context.Validated context) {
    var now = Instant.now(clock);

    var updatedCart =
        context
            .cart()
            .addItem(
                context.productId(),
                context.productName(),
                context.unitPrice(),
                context.quantity(),
                now);

    return Mono.just(new AddItemToCartWorkflow.Context.Added(updatedCart));
  }
}
