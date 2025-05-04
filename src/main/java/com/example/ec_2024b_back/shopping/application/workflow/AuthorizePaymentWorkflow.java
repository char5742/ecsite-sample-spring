package com.example.ec_2024b_back.shopping.application.workflow;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.shopping.domain.models.Payment;
import com.example.ec_2024b_back.shopping.domain.models.PaymentId;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Mono;

/** 支払いを承認するワークフローのインターフェース */
public interface AuthorizePaymentWorkflow {

  /** 支払いを取得するステップ */
  @FunctionalInterface
  interface GetPaymentStep extends Function<Context.Input, Mono<Context.PaymentFound>> {}

  /** 支払いのアクセス権を検証するステップ */
  @FunctionalInterface
  interface VerifyAccessStep extends Function<Context.PaymentFound, Mono<Context.Verified>> {}

  /** 支払いを承認するステップ */
  @FunctionalInterface
  interface AuthorizePaymentStep extends Function<Context.Verified, Mono<Context.Authorized>> {}

  /** 支払いを保存するステップ */
  @FunctionalInterface
  interface SavePaymentStep extends Function<Context.Authorized, Mono<Context.Complete>> {}

  /** ワークフローコンテキスト */
  sealed interface Context
      permits Context.Input,
          Context.PaymentFound,
          Context.Verified,
          Context.Authorized,
          Context.Complete {

    /**
     * 入力コンテキスト
     *
     * @param paymentId 支払いID
     * @param accountId アカウントID
     * @param externalTransactionId 外部トランザクションID
     */
    record Input(PaymentId paymentId, AccountId accountId, @Nullable String externalTransactionId)
        implements Context {}

    /**
     * 支払い見つかりコンテキスト
     *
     * @param payment 支払い
     * @param accountId アカウントID
     * @param externalTransactionId 外部トランザクションID
     */
    record PaymentFound(
        Payment payment, AccountId accountId, @Nullable String externalTransactionId)
        implements Context {}

    /**
     * アクセス検証済みコンテキスト
     *
     * @param payment 支払い
     * @param externalTransactionId 外部トランザクションID
     */
    record Verified(Payment payment, @Nullable String externalTransactionId) implements Context {}

    /**
     * 支払い承認済みコンテキスト
     *
     * @param payment 承認された支払い
     */
    record Authorized(Payment payment) implements Context {}

    /**
     * 完了コンテキスト
     *
     * @param payment 保存された支払い
     */
    record Complete(Payment payment) implements Context {}
  }

  /**
   * 支払いを承認します
   *
   * @param paymentId 支払いID
   * @param accountId アカウントID
   * @param externalTransactionId 外部トランザクションID
   * @return 更新された支払い
   */
  Mono<Payment> execute(
      PaymentId paymentId, AccountId accountId, @Nullable String externalTransactionId);

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

  /** 支払い状態が無効な場合の例外 */
  class InvalidPaymentStateException extends DomainException {
    public InvalidPaymentStateException(String message) {
      super(message);
    }
  }
}
