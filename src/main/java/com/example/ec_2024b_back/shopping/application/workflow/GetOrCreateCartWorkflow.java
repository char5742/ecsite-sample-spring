package com.example.ec_2024b_back.shopping.application.workflow;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** カート取得または作成ワークフローのインターフェース */
public interface GetOrCreateCartWorkflow {

  /** ユーザーのカートを取得するステップ */
  @FunctionalInterface
  interface FindCartStep extends Function<Context.Input, Mono<Context.CartFound>> {}

  /** カートを作成するステップ */
  @FunctionalInterface
  interface CreateCartStep extends Function<Context.Input, Mono<Context.Created>> {}

  /** ワークフローコンテキスト */
  sealed interface Context permits Context.Input, Context.CartFound, Context.Created {

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
     * カート作成済みコンテキスト
     *
     * @param cart 作成されたカート
     */
    record Created(Cart cart) implements Context {}
  }

  /**
   * ユーザーのカートを取得します。存在しない場合は新しいカートを作成します。
   *
   * @param accountId アカウントID
   * @return 取得または作成されたカート
   */
  Mono<Cart> execute(AccountId accountId);

  /** カートが見つからない場合の例外 */
  class CartNotFoundException extends DomainException {
    public CartNotFoundException(String message) {
      super(message);
    }
  }
}
