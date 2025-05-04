package com.example.ec_2024b_back.shopping.application.workflow;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.shopping.domain.models.OrderId;
import com.example.ec_2024b_back.shopping.domain.models.Payment;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** 支払い詳細を取得するワークフローのインターフェース */
public interface GetPaymentDetailsWorkflow {

  /** 支払いを取得するステップ */
  @FunctionalInterface
  interface GetPaymentStep extends Function<Context.Input, Mono<Context.PaymentFound>> {}

  /** 支払いのアクセス権を検証するステップ */
  @FunctionalInterface
  interface VerifyAccessStep extends Function<Context.PaymentFound, Mono<Context.Complete>> {}

  /** ワークフローコンテキスト */
  sealed interface Context permits Context.Input, Context.PaymentFound, Context.Complete {

    /**
     * 入力コンテキスト
     *
     * @param orderId 注文ID
     * @param accountId アカウントID
     */
    record Input(OrderId orderId, AccountId accountId) implements Context {}

    /**
     * 支払い見つかりコンテキスト
     *
     * @param payment 支払い
     * @param accountId アカウントID
     */
    record PaymentFound(Payment payment, AccountId accountId) implements Context {}

    /**
     * 完了コンテキスト
     *
     * @param payment 支払い
     */
    record Complete(Payment payment) implements Context {}
  }

  /**
   * 注文の支払い詳細を取得します
   *
   * @param orderId 注文ID
   * @param accountId アカウントID
   * @return 支払い
   */
  Mono<Payment> execute(OrderId orderId, AccountId accountId);

  /** 支払いが見つからない場合の例外 */
  class PaymentNotFoundException extends DomainException {
    public PaymentNotFoundException(String message) {
      super(message);
    }
  }

  /** 支払いへのアクセス権がない場合の例外 */
  class UnauthorizedPaymentAccessException extends DomainException {
    public UnauthorizedPaymentAccessException(String message) {
      super(message);
    }
  }
}
