package com.example.ec_2024b_back.shopping.application.workflow;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** カートを空にするワークフローのインターフェース */
public interface ClearCartWorkflow {

  /** ユーザーのカートを取得するステップ */
  @FunctionalInterface
  interface GetCartStep extends Function<Context.Input, Mono<Context.CartFound>> {}

  /** カートを空にするステップ */
  @FunctionalInterface
  interface ClearCartStep extends Function<Context.CartFound, Mono<Context.Cleared>> {}

  /** カートを保存するステップ */
  @FunctionalInterface
  interface SaveCartStep extends Function<Context.Cleared, Mono<Context.Complete>> {}

  /** ワークフローコンテキスト */
  sealed interface Context
      permits Context.Input, Context.CartFound, Context.Cleared, Context.Complete {

    /**
     * 入力コンテキスト
     *
     * @param accountId アカウントID
     */
    record Input(AccountId accountId) implements Context {}

    /**
     * カート見つかりコンテキスト
     *
     * @param cart カート
     */
    record CartFound(Cart cart) implements Context {}

    /**
     * カートクリア済みコンテキスト
     *
     * @param cart クリア済みカート
     */
    record Cleared(Cart cart) implements Context {}

    /**
     * 完了コンテキスト
     *
     * @param cart 保存済みカート
     */
    record Complete(Cart cart) implements Context {}
  }

  /**
   * カートを空にします
   *
   * @param accountId アカウントID
   * @return 更新されたカート
   */
  Mono<Cart> execute(AccountId accountId);

  /** カートが見つからない場合の例外 */
  class CartNotFoundException extends DomainException {
    public CartNotFoundException(String message) {
      super(message);
    }
  }
}
