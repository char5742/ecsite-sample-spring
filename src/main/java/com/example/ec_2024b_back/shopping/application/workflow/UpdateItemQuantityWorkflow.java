package com.example.ec_2024b_back.shopping.application.workflow;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** カート内の商品数量を更新するワークフローのインターフェース */
public interface UpdateItemQuantityWorkflow {

  /** ユーザーのカートを取得するステップ */
  @FunctionalInterface
  interface GetCartStep extends Function<Context.Input, Mono<Context.CartFound>> {}

  /** 数量を検証するステップ */
  @FunctionalInterface
  interface ValidateQuantityStep extends Function<Context.CartFound, Mono<Context.Validated>> {}

  /** カート内の商品数量を更新するステップ */
  @FunctionalInterface
  interface UpdateQuantityStep extends Function<Context.Validated, Mono<Context.Updated>> {}

  /** カートを保存するステップ */
  @FunctionalInterface
  interface SaveCartStep extends Function<Context.Updated, Mono<Context.Complete>> {}

  /** ワークフローコンテキスト */
  sealed interface Context
      permits Context.Input,
          Context.CartFound,
          Context.Validated,
          Context.Updated,
          Context.Complete {

    /**
     * 入力コンテキスト
     *
     * @param accountId アカウントID
     * @param productId 商品ID
     * @param quantity 新しい数量
     */
    record Input(AccountId accountId, ProductId productId, int quantity) implements Context {}

    /**
     * カート見つかりコンテキスト
     *
     * @param cart カート
     * @param productId 商品ID
     * @param quantity 新しい数量
     */
    record CartFound(Cart cart, ProductId productId, int quantity) implements Context {}

    /**
     * 検証済みコンテキスト
     *
     * @param cart カート
     * @param productId 商品ID
     * @param quantity 新しい数量
     */
    record Validated(Cart cart, ProductId productId, int quantity) implements Context {}

    /**
     * 数量更新済みコンテキスト
     *
     * @param cart 数量更新済みカート
     */
    record Updated(Cart cart) implements Context {}

    /**
     * 完了コンテキスト
     *
     * @param cart 保存済みカート
     */
    record Complete(Cart cart) implements Context {}
  }

  /**
   * カート内の商品数量を更新します
   *
   * @param accountId アカウントID
   * @param productId 商品ID
   * @param quantity 新しい数量
   * @return 更新されたカート
   */
  Mono<Cart> execute(AccountId accountId, ProductId productId, int quantity);

  /** カートが見つからない場合の例外 */
  class CartNotFoundException extends DomainException {
    public CartNotFoundException(String message) {
      super(message);
    }
  }

  /** 商品がカート内に見つからない場合の例外 */
  class ProductNotInCartException extends DomainException {
    public ProductNotInCartException(String message) {
      super(message);
    }
  }

  /** 数量が無効な場合の例外 */
  class InvalidQuantityException extends DomainException {
    public InvalidQuantityException(String message) {
      super(message);
    }
  }
}
