package com.example.ec_2024b_back.shopping.application.workflow;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.shopping.domain.models.Order;
import com.example.ec_2024b_back.shopping.domain.models.OrderId;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** 注文をキャンセルするワークフローのインターフェース */
public interface CancelOrderWorkflow {

  /** 注文を取得するステップ */
  @FunctionalInterface
  interface GetOrderStep extends Function<Context.Input, Mono<Context.OrderFound>> {}

  /** 注文のアクセス権を検証するステップ */
  @FunctionalInterface
  interface VerifyAccessStep extends Function<Context.OrderFound, Mono<Context.Verified>> {}

  /** 注文をキャンセルするステップ */
  @FunctionalInterface
  interface CancelOrderStep extends Function<Context.Verified, Mono<Context.Cancelled>> {}

  /** 注文を保存するステップ */
  @FunctionalInterface
  interface SaveOrderStep extends Function<Context.Cancelled, Mono<Context.Complete>> {}

  /** ワークフローコンテキスト */
  sealed interface Context
      permits Context.Input,
          Context.OrderFound,
          Context.Verified,
          Context.Cancelled,
          Context.Complete {

    /**
     * 入力コンテキスト
     *
     * @param orderId 注文ID
     * @param accountId アカウントID
     * @param reason キャンセル理由
     */
    record Input(OrderId orderId, AccountId accountId, String reason) implements Context {}

    /**
     * 注文見つかりコンテキスト
     *
     * @param order 注文
     * @param accountId アカウントID
     * @param reason キャンセル理由
     */
    record OrderFound(Order order, AccountId accountId, String reason) implements Context {}

    /**
     * アクセス検証済みコンテキスト
     *
     * @param order 注文
     * @param reason キャンセル理由
     */
    record Verified(Order order, String reason) implements Context {}

    /**
     * キャンセル済みコンテキスト
     *
     * @param order キャンセルされた注文
     */
    record Cancelled(Order order) implements Context {}

    /**
     * 完了コンテキスト
     *
     * @param order 保存された注文
     */
    record Complete(Order order) implements Context {}
  }

  /**
   * 注文をキャンセルします
   *
   * @param orderId 注文ID
   * @param accountId アカウントID
   * @param reason キャンセル理由
   * @return 更新された注文
   */
  Mono<Order> execute(OrderId orderId, AccountId accountId, String reason);

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

  /** 注文状態によりキャンセルできない場合の例外 */
  class OrderCannotBeCancelledException extends DomainException {
    public OrderCannotBeCancelledException(String message) {
      super(message);
    }
  }
}
