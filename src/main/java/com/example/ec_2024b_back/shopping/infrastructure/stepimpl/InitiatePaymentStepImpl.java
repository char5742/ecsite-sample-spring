package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.shopping.PaymentId;
import com.example.ec_2024b_back.shopping.application.workflow.InitiatePaymentWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.InitiatePaymentWorkflow.InitiatePaymentStep;
import com.example.ec_2024b_back.shopping.domain.models.Payment;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 支払いを開始するステップの実装 */
@Component
@RequiredArgsConstructor
public class InitiatePaymentStepImpl implements InitiatePaymentStep {

  private final IdGenerator idGenerator;
  private final Clock clock = Clock.system(ZoneId.systemDefault());

  @Override
  public Mono<InitiatePaymentWorkflow.Context.Initiated> apply(
      InitiatePaymentWorkflow.Context.Checked context) {
    var now = Instant.now(clock);

    // 支払いIDの生成
    var paymentId = new PaymentId(idGenerator.newId());

    // 支払いの作成
    var payment =
        Payment.initiate(
            paymentId,
            context.order().getId(),
            context.order().getTotalAmount(),
            context.paymentMethod(),
            now);

    return Mono.just(new InitiatePaymentWorkflow.Context.Initiated(payment, context.order()));
  }
}
