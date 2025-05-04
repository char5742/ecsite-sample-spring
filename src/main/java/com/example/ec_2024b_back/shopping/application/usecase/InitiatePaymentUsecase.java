package com.example.ec_2024b_back.shopping.application.usecase;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.OrderId;
import com.example.ec_2024b_back.shopping.application.workflow.InitiatePaymentWorkflow;
import com.example.ec_2024b_back.shopping.domain.models.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/** 支払い開始ユースケースを実装するクラス. */
@Service
@RequiredArgsConstructor
public class InitiatePaymentUsecase {

  private final InitiatePaymentWorkflow initiatePaymentWorkflow;
  private final ApplicationEventPublisher event;

  /**
   * 支払い開始処理を実行し、作成された支払いを返すMonoを返します.
   *
   * @param orderId 注文ID
   * @param accountId アカウントID
   * @param paymentMethod 支払い方法
   * @return 作成された支払いを含むMono
   */
  @Transactional
  public Mono<Payment> execute(OrderId orderId, AccountId accountId, String paymentMethod) {
    return initiatePaymentWorkflow
        .execute(orderId, accountId, paymentMethod)
        .onErrorMap(
            e ->
                new PaymentOperationFailedException(
                    "Failed to initiate payment: " + e.getMessage(), e))
        .doOnNext(payment -> payment.getEvents().forEach(event::publishEvent));
  }

  /** 支払い操作失敗を表すカスタム例外. */
  public static class PaymentOperationFailedException extends RuntimeException {
    public PaymentOperationFailedException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
