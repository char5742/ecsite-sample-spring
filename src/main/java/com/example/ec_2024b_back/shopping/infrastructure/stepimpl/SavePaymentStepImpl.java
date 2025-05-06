package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.shopping.application.workflow.InitiatePaymentWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.InitiatePaymentWorkflow.SavePaymentStep;
import com.example.ec_2024b_back.shopping.domain.repositories.Payments;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 支払いを保存するステップの実装 */
@Component
@RequiredArgsConstructor
public class SavePaymentStepImpl implements SavePaymentStep {

  private final Payments payments;

  @Override
  public Mono<InitiatePaymentWorkflow.Context.Complete> apply(
      InitiatePaymentWorkflow.Context.Initiated context) {
    return payments.save(context.payment()).map(InitiatePaymentWorkflow.Context.Complete::new);
  }
}
