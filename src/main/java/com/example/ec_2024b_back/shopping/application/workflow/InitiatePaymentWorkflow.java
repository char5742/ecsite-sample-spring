package com.example.ec_2024b_back.shopping.application.workflow;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.shopping.OrderId;
import com.example.ec_2024b_back.shopping.domain.models.Order;
import com.example.ec_2024b_back.shopping.domain.models.Payment;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** 支払いを開始するワークフローのインターフェース */
public interface InitiatePaymentWorkflow {

  /** 注文を取得するステップ */
  @FunctionalInterface
  interface GetOrderStep extends Function<Context.Input, Mono<Context.OrderFound>> {}

  /** 注文のアクセス権を検証するステップ */
  @FunctionalInterface
  interface VerifyAccessStep extends Function<Context.OrderFound, Mono<Context.Verified>> {}

  /** 既存の支払いを確認するステップ */
  @FunctionalInterface
  interface CheckExistingPaymentStep extends Function<Context.Verified, Mono<Context.Checked>> {}

  /** 支払いを開始するステップ */
  @FunctionalInterface
  interface InitiatePaymentStep extends Function<Context.Checked, Mono<Context.Initiated>> {}

  /** 支払いを保存するステップ */
  @FunctionalInterface
  interface SavePaymentStep extends Function<Context.Initiated, Mono<Context.Complete>> {}

  /** ワークフローコンテキスト */
  sealed interface Context
      permits Context.Input,
          Context.OrderFound,
          Context.Verified,
          Context.Checked,
          Context.Initiated,
          Context.Complete {

    /**
     * 入力コンテキスト
     *
     * @param orderId 注文ID
     * @param accountId アカウントID
     * @param paymentMethod 支払い方法
     */
    record Input(OrderId orderId, AccountId accountId, String paymentMethod) implements Context {}

    /**
     * 注文見つかりコンテキスト
     *
     * @param order 注文
     * @param accountId アカウントID
     * @param paymentMethod 支払い方法
     */
    record OrderFound(Order order, AccountId accountId, String paymentMethod) implements Context {}

    /**
     * アクセス検証済みコンテキスト
     *
     * @param order 注文
     * @param paymentMethod 支払い方法
     */
    record Verified(Order order, String paymentMethod) implements Context {}

    /**
     * 支払い確認済みコンテキスト
     *
     * @param order 注文
     * @param paymentMethod 支払い方法
     */
    record Checked(Order order, String paymentMethod) implements Context {}

    /**
     * 支払い開始済みコンテキスト
     *
     * @param payment 支払い
     * @param order 注文
     */
    record Initiated(Payment payment, Order order) implements Context {}

    /**
     * 完了コンテキスト
     *
     * @param payment 保存された支払い
     */
    record Complete(Payment payment) implements Context {}
  }

  /**
   * 注文に対する支払いを開始します
   *
   * @param orderId 注文ID
   * @param accountId アカウントID
   * @param paymentMethod 支払い方法
   * @return 作成された支払い
   */
  Mono<Payment> execute(OrderId orderId, AccountId accountId, String paymentMethod);

  /** 注文が見つからない場合の例外 */
  class OrderNotFoundException extends DomainException {
    public OrderNotFoundException(String message) {
      super(message);
    }
  }

  /** 注文へのアクセス権がない場合の例外 */
  class UnauthorizedOrderAccessException extends DomainException {
    public UnauthorizedOrderAccessException(String message) {
      super(message);
    }
  }

  /** 支払いが既に存在する場合の例外 */
  class PaymentAlreadyExistsException extends DomainException {
    public PaymentAlreadyExistsException(String message) {
      super(message);
    }
  }

  /** 支払い方法が無効な場合の例外 */
  class InvalidPaymentMethodException extends DomainException {
    public InvalidPaymentMethodException(String message) {
      super(message);
    }
  }
}
