package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.shopping.application.workflow.InitiatePaymentWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.InitiatePaymentWorkflow.GetOrderStep;
import com.example.ec_2024b_back.shopping.domain.repositories.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 注文を取得するステップの実装 */
@Component
@RequiredArgsConstructor
public class GetOrderStepImpl implements GetOrderStep {

  private final Orders orders;

  @Override
  public Mono<InitiatePaymentWorkflow.Context.OrderFound> apply(
      InitiatePaymentWorkflow.Context.Input context) {
    return orders
        .findById(context.orderId())
        .switchIfEmpty(
            Mono.error(
                new InitiatePaymentWorkflow.OrderNotFoundException(
                    "注文ID: " + context.orderId() + " の注文が見つかりません")))
        .map(
            order ->
                new InitiatePaymentWorkflow.Context.OrderFound(
                    order, context.accountId(), context.paymentMethod()));
  }
}
