package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.shopping.application.workflow.CreateOrderFromCartWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.CreateOrderFromCartWorkflow.ClearCartStep;
import com.example.ec_2024b_back.shopping.domain.repositories.Carts;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** カートの内容をクリアするステップの実装 */
@Component
@RequiredArgsConstructor
public class ClearCartStepImpl implements ClearCartStep {

  private final Carts carts;
  private final Clock clock = Clock.system(ZoneId.systemDefault());

  @Override
  public Mono<CreateOrderFromCartWorkflow.Context.CartCleared> apply(
      CreateOrderFromCartWorkflow.Context.Created context) {
    var now = Instant.now(clock);

    // カートの内容をクリア
    var clearedCart = context.cart().clear(now);

    // クリアしたカートを保存
    return carts
        .save(clearedCart)
        .map(
            savedCart ->
                new CreateOrderFromCartWorkflow.Context.CartCleared(context.order(), savedCart));
  }
}
