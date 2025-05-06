package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.shopping.application.workflow.CreateOrderFromCartWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.CreateOrderFromCartWorkflow.SaveOrderStep;
import com.example.ec_2024b_back.shopping.domain.repositories.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 注文を保存するステップの実装 */
@Component
@RequiredArgsConstructor
public class SaveOrderStepImpl implements SaveOrderStep {

  private final Orders orders;

  @Override
  public Mono<CreateOrderFromCartWorkflow.Context.Complete> apply(
      CreateOrderFromCartWorkflow.Context.CartCleared context) {
    return orders.save(context.order()).map(CreateOrderFromCartWorkflow.Context.Complete::new);
  }
}
