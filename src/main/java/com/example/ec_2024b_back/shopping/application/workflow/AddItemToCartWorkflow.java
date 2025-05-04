package com.example.ec_2024b_back.shopping.application.workflow;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import java.math.BigDecimal;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** カートに商品を追加するワークフローのインターフェース */
public interface AddItemToCartWorkflow {

  /** ユーザーのカートを取得するステップ */
  @FunctionalInterface
  interface GetCartStep extends Function<Context.Input, Mono<Context.CartFound>> {}

  /** 商品情報を検証するステップ */
  @FunctionalInterface
  interface ValidateProductStep extends Function<Context.CartFound, Mono<Context.Validated>> {}

  /** カートに商品を追加するステップ */
  @FunctionalInterface
  interface AddItemStep extends Function<Context.Validated, Mono<Context.Added>> {}

  /** カートを保存するステップ */
  @FunctionalInterface
  interface SaveCartStep extends Function<Context.Added, Mono<Context.Complete>> {}

  /** ワークフローコンテキスト */
  sealed interface Context
      permits Context.Input, Context.CartFound, Context.Validated, Context.Added, Context.Complete {

    /**
     * 入力コンテキスト
     *
     * @param accountId アカウントID
     * @param productId 商品ID
     * @param productName 商品名
     * @param unitPrice 単価
     * @param quantity 数量
     */
    record Input(
        AccountId accountId,
        ProductId productId,
        String productName,
        BigDecimal unitPrice,
        int quantity)
        implements Context {}

    /**
     * カート見つかりコンテキスト
     *
     * @param cart カート
     * @param productId 商品ID
     * @param productName 商品名
     * @param unitPrice 単価
     * @param quantity 数量
     */
    record CartFound(
        Cart cart, ProductId productId, String productName, BigDecimal unitPrice, int quantity)
        implements Context {}

    /**
     * 検証済みコンテキスト
     *
     * @param cart カート
     * @param productId 商品ID
     * @param productName 商品名
     * @param unitPrice 単価
     * @param quantity 数量
     */
    record Validated(
        Cart cart, ProductId productId, String productName, BigDecimal unitPrice, int quantity)
        implements Context {}

    /**
     * 商品追加済みコンテキスト
     *
     * @param cart 商品追加済みカート
     */
    record Added(Cart cart) implements Context {}

    /**
     * 完了コンテキスト
     *
     * @param cart 保存済みカート
     */
    record Complete(Cart cart) implements Context {}
  }

  /**
   * カートに商品を追加します
   *
   * @param accountId アカウントID
   * @param productId 商品ID
   * @param productName 商品名
   * @param unitPrice 単価
   * @param quantity 数量
   * @return 更新されたカート
   */
  Mono<Cart> execute(
      AccountId accountId,
      ProductId productId,
      String productName,
      BigDecimal unitPrice,
      int quantity);

  /** カートが見つからない場合の例外 */
  class CartNotFoundException extends DomainException {
    public CartNotFoundException(String message) {
      super(message);
    }
  }

  /** 商品情報が無効な場合の例外 */
  class InvalidProductInfoException extends DomainException {
    public InvalidProductInfoException(String message) {
      super(message);
    }
  }
}
