package com.example.ec_2024b_back.shopping.application.workflow;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.shopping.domain.models.Payment;
import com.example.ec_2024b_back.shopping.domain.models.PaymentId;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Mono;

/** 支払いを失敗状態に更新するワークフローのインターフェース */
public interface FailPaymentWorkflow {

  /** 支払いを取得するステップ */
  @FunctionalInterface
  interface GetPaymentStep extends Function<Context.Input, Mono<Context.PaymentFound>> {}

  /** 支払いのアクセス権を検証するステップ */
  @FunctionalInterface
  interface VerifyAccessStep extends Function<Context.PaymentFound, Mono<Context.Verified>> {}

  /** 支払いを失敗状態に更新するステップ */
  @FunctionalInterface
  interface FailPaymentStep extends Function<Context.Verified, Mono<Context.Failed>> {}

  /** 支払いを保存するステップ */
  @FunctionalInterface
  interface SavePaymentStep extends Function<Context.Failed, Mono<Context.Complete>> {}

  /** ワークフローコンテキスト */
  sealed interface Context
      permits Context.Input,
          Context.PaymentFound,
          Context.Verified,
          Context.Failed,
          Context.Complete {

    /**
     * 入力コンテキスト
     *
     * @param paymentId 支払いID
     * @param accountId アカウントID
     * @param errorCode エラーコード
     * @param errorMessage エラーメッセージ
     */
    record Input(
        PaymentId paymentId, AccountId accountId, @Nullable String errorCode, String errorMessage)
        implements Context {}

    /**
     * 支払い見つかりコンテキスト
     *
     * @param payment 支払い
     * @param accountId アカウントID
     * @param errorCode エラーコード
     * @param errorMessage エラーメッセージ
     */
    record PaymentFound(
        Payment payment, AccountId accountId, @Nullable String errorCode, String errorMessage)
        implements Context {}

    /**
     * アクセス検証済みコンテキスト
     *
     * @param payment 支払い
     * @param errorCode エラーコード
     * @param errorMessage エラーメッセージ
     */
    record Verified(Payment payment, @Nullable String errorCode, String errorMessage)
        implements Context {}

    /**
     * 支払い失敗状態更新済みコンテキスト
     *
     * @param payment 失敗状態に更新された支払い
     */
    record Failed(Payment payment) implements Context {}

    /**
     * 完了コンテキスト
     *
     * @param payment 保存された支払い
     */
    record Complete(Payment payment) implements Context {}
  }

  /**
   * 支払いを失敗状態に更新します
   *
   * @param paymentId 支払いID
   * @param accountId アカウントID
   * @param errorCode エラーコード
   * @param errorMessage エラーメッセージ
   * @return 更新された支払い
   */
  Mono<Payment> execute(
      PaymentId paymentId, AccountId accountId, @Nullable String errorCode, String errorMessage);

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
