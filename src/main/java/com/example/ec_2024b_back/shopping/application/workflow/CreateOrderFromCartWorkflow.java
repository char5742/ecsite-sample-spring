package com.example.ec_2024b_back.shopping.application.workflow;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import com.example.ec_2024b_back.shopping.domain.models.Order;
import java.math.BigDecimal;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** カートから注文を作成するワークフローのインターフェース */
public interface CreateOrderFromCartWorkflow {

  /** ユーザーのカートを取得するステップ */
  @FunctionalInterface
  interface GetCartStep extends Function<Context.Input, Mono<Context.CartFound>> {}

  /** カートの内容を検証するステップ */
  @FunctionalInterface
  interface ValidateCartStep extends Function<Context.CartFound, Mono<Context.Validated>> {}

  /** 注文を作成するステップ */
  @FunctionalInterface
  interface CreateOrderStep extends Function<Context.Validated, Mono<Context.Created>> {}

  /** カートの内容をクリアするステップ */
  @FunctionalInterface
  interface ClearCartStep extends Function<Context.Created, Mono<Context.CartCleared>> {}

  /** 注文を保存するステップ */
  @FunctionalInterface
  interface SaveOrderStep extends Function<Context.CartCleared, Mono<Context.Complete>> {}

  /** ワークフローコンテキスト */
  sealed interface Context
      permits Context.Input,
          Context.CartFound,
          Context.Validated,
          Context.Created,
          Context.CartCleared,
          Context.Complete {

    /**
     * 入力コンテキスト
     *
     * @param accountId アカウントID
     * @param shippingAddress 配送先住所
     */
    record Input(AccountId accountId, String shippingAddress) implements Context {}

    /**
     * カート見つかりコンテキスト
     *
     * @param cart カート
     * @param shippingAddress 配送先住所
     */
    record CartFound(Cart cart, String shippingAddress) implements Context {}

    /**
     * 検証済みコンテキスト
     *
     * @param cart カート
     * @param shippingAddress 配送先住所
     * @param shippingCost 配送料
     * @param taxRate 税率
     */
    record Validated(Cart cart, String shippingAddress, BigDecimal shippingCost, BigDecimal taxRate)
        implements Context {}

    /**
     * 注文作成済みコンテキスト
     *
     * @param order 作成された注文
     * @param cart カート
     */
    record Created(Order order, Cart cart) implements Context {}

    /**
     * カートクリア済みコンテキスト
     *
     * @param order 作成された注文
     * @param cart クリア済みカート
     */
    record CartCleared(Order order, Cart cart) implements Context {}

    /**
     * 完了コンテキスト
     *
     * @param order 保存された注文
     */
    record Complete(Order order) implements Context {}
  }

  /**
   * カートの内容から注文を作成します
   *
   * @param accountId アカウントID
   * @param shippingAddress 配送先住所
   * @return 作成された注文
   */
  Mono<Order> execute(AccountId accountId, String shippingAddress);

  /** カートが見つからない場合の例外 */
  class CartNotFoundException extends DomainException {
    public CartNotFoundException(String message) {
      super(message);
    }
  }

  /** カートが空の場合の例外 */
  class EmptyCartException extends DomainException {
    public EmptyCartException(String message) {
      super(message);
    }
  }

  /** 配送先住所が無効な場合の例外 */
  class InvalidShippingAddressException extends DomainException {
    public InvalidShippingAddressException(String message) {
      super(message);
    }
  }
}
