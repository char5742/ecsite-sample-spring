package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.shopping.application.workflow.InitiatePaymentWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.InitiatePaymentWorkflow.CheckExistingPaymentStep;
import com.example.ec_2024b_back.shopping.domain.models.OrderStatus;
import com.example.ec_2024b_back.shopping.domain.repositories.Payments;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 既存の支払いを確認するステップの実装 */
@Component
@RequiredArgsConstructor
public class CheckExistingPaymentStepImpl implements CheckExistingPaymentStep {

  private final Payments payments;

  @Override
  public Mono<InitiatePaymentWorkflow.Context.Checked> apply(
      InitiatePaymentWorkflow.Context.Verified context) {
    // 支払い方法のバリデーション
    if (context.paymentMethod() == null || context.paymentMethod().isBlank()) {
      return Mono.error(new InitiatePaymentWorkflow.InvalidPaymentMethodException("支払い方法は必須です"));
    }

    // 注文の状態を確認
    if (context.order().getStatus() != OrderStatus.CREATED) {
      return Mono.error(
          new InitiatePaymentWorkflow.InvalidPaymentMethodException(
              "この注文の状態では支払いを開始できません: " + context.order().getStatus()));
    }

    // 既存の支払いがないことを確認
    return payments
        .findByOrderId(context.order().getId())
        .hasElement()
        .flatMap(
            hasPayment -> {
              if (hasPayment) {
                return Mono.error(
                    new InitiatePaymentWorkflow.PaymentAlreadyExistsException(
                        "この注文に対する支払いはすでに存在します"));
              }
              return Mono.just(
                  new InitiatePaymentWorkflow.Context.Checked(
                      context.order(), context.paymentMethod()));
            });
  }
}
