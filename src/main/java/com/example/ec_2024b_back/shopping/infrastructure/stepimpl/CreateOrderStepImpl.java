package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.shopping.OrderId;
import com.example.ec_2024b_back.shopping.application.workflow.CreateOrderFromCartWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.CreateOrderFromCartWorkflow.CreateOrderStep;
import com.example.ec_2024b_back.shopping.domain.models.Order;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 注文を作成するステップの実装 */
@Component
@RequiredArgsConstructor
public class CreateOrderStepImpl implements CreateOrderStep {

  private final IdGenerator idGenerator;
  private final Clock clock = Clock.system(ZoneId.systemDefault());

  @Override
  public Mono<CreateOrderFromCartWorkflow.Context.Created> apply(
      CreateOrderFromCartWorkflow.Context.Validated context) {
    var now = Instant.now(clock);

    // 注文IDの生成
    var orderId = new OrderId(idGenerator.newId());

    // 注文の作成
    var order =
        Order.createFromCart(
            orderId,
            context.cart(),
            context.shippingAddress(),
            context.shippingCost(),
            context.taxRate(),
            now);

    return Mono.just(new CreateOrderFromCartWorkflow.Context.Created(order, context.cart()));
  }
}
