package com.example.ec_2024b_back.shopping.infrastructure.workflowimpl;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.OrderId;
import com.example.ec_2024b_back.shopping.application.workflow.InitiatePaymentWorkflow;
import com.example.ec_2024b_back.shopping.domain.models.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 支払いを開始するワークフローの実装 */
@Component
@RequiredArgsConstructor
public class InitiatePaymentWorkflowImpl implements InitiatePaymentWorkflow {

  private final GetOrderStep getOrderStep;
  private final VerifyAccessStep verifyAccessStep;
  private final CheckExistingPaymentStep checkExistingPaymentStep;
  private final InitiatePaymentStep initiatePaymentStep;
  private final SavePaymentStep savePaymentStep;

  @Override
  public Mono<Payment> execute(OrderId orderId, AccountId accountId, String paymentMethod) {
    var input = new Context.Input(orderId, accountId, paymentMethod);

    return getOrderStep
        .apply(input)
        .flatMap(verifyAccessStep)
        .flatMap(checkExistingPaymentStep)
        .flatMap(initiatePaymentStep)
        .flatMap(savePaymentStep)
        .map(Context.Complete::payment);
  }
}
