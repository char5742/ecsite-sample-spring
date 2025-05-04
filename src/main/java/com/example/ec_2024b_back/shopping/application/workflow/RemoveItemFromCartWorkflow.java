package com.example.ec_2024b_back.shopping.application.workflow;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** カートから商品を削除するワークフローのインターフェース */
public interface RemoveItemFromCartWorkflow {

  /** ユーザーのカートを取得するステップ */
  @FunctionalInterface
  interface GetCartStep extends Function<Context.Input, Mono<Context.CartFound>> {}

  /** カートから商品を削除するステップ */
  @FunctionalInterface
  interface RemoveItemStep extends Function<Context.CartFound, Mono<Context.Removed>> {}

  /** カートを保存するステップ */
  @FunctionalInterface
  interface SaveCartStep extends Function<Context.Removed, Mono<Context.Complete>> {}

  /** ワークフローコンテキスト */
  sealed interface Context
      permits Context.Input, Context.CartFound, Context.Removed, Context.Complete {

    /**
     * 入力コンテキスト
     *
     * @param accountId アカウントID
     * @param productId 商品ID
     */
    record Input(AccountId accountId, ProductId productId) implements Context {}

    /**
     * カート見つかりコンテキスト
     *
     * @param cart カート
     * @param productId 商品ID
     */
    record CartFound(Cart cart, ProductId productId) implements Context {}

    /**
     * 商品削除済みコンテキスト
     *
     * @param cart 商品削除済みカート
     */
    record Removed(Cart cart) implements Context {}

    /**
     * 完了コンテキスト
     *
     * @param cart 保存済みカート
     */
    record Complete(Cart cart) implements Context {}
  }

  /**
   * カートから商品を削除します
   *
   * @param accountId アカウントID
   * @param productId 商品ID
   * @return 更新されたカート
   */
  Mono<Cart> execute(AccountId accountId, ProductId productId);

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
}
