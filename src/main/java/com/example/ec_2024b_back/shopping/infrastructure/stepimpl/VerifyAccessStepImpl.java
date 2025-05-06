package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.shopping.application.workflow.InitiatePaymentWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.InitiatePaymentWorkflow.VerifyAccessStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 注文へのアクセス権を検証するステップの実装 */
@Component
@RequiredArgsConstructor
public class VerifyAccessStepImpl implements VerifyAccessStep {

  @Override
  public Mono<InitiatePaymentWorkflow.Context.Verified> apply(
      InitiatePaymentWorkflow.Context.OrderFound context) {
    // 注文の所有者が現在のユーザーと一致するか確認
    if (!context.order().getAccountId().equals(context.accountId())) {
      return Mono.error(
          new InitiatePaymentWorkflow.UnauthorizedOrderAccessException("この注文に対するアクセス権限がありません"));
    }

    return Mono.just(
        new InitiatePaymentWorkflow.Context.Verified(context.order(), context.paymentMethod()));
  }
}
