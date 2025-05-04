package com.example.ec_2024b_back.shopping.application.workflow;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.shopping.domain.models.Order;
import com.example.ec_2024b_back.shopping.domain.models.OrderId;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** 注文詳細を取得するワークフローのインターフェース */
public interface GetOrderDetailsWorkflow {

  /** 注文を取得するステップ */
  @FunctionalInterface
  interface GetOrderStep extends Function<Context.Input, Mono<Context.OrderFound>> {}

  /** 注文のアクセス権を検証するステップ */
  @FunctionalInterface
  interface VerifyAccessStep extends Function<Context.OrderFound, Mono<Context.Complete>> {}

  /** ワークフローコンテキスト */
  sealed interface Context permits Context.Input, Context.OrderFound, Context.Complete {

    /**
     * 入力コンテキスト
     *
     * @param orderId 注文ID
     * @param accountId アカウントID
     */
    record Input(OrderId orderId, AccountId accountId) implements Context {}

    /**
     * 注文見つかりコンテキスト
     *
     * @param order 注文
     * @param accountId アカウントID
     */
    record OrderFound(Order order, AccountId accountId) implements Context {}

    /**
     * 完了コンテキスト
     *
     * @param order 注文
     */
    record Complete(Order order) implements Context {}
  }

  /**
   * 注文詳細を取得します
   *
   * @param orderId 注文ID
   * @param accountId アカウントID
   * @return 注文
   */
  Mono<Order> execute(OrderId orderId, AccountId accountId);

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
}
