package com.example.ec_2024b_back.shopping.domain.repositories;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.CartId;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import org.jmolecules.ddd.types.Repository;
import reactor.core.publisher.Mono;

/** カートリポジトリのインターフェース */
public interface Carts extends Repository<Cart, CartId> {

  /**
   * カートIDによりカートを検索します
   *
   * @param id カートID
   * @return 検索結果
   */
  Mono<Cart> findById(CartId id);

  /**
   * アカウントIDによりカートを検索します
   *
   * @param accountId アカウントID
   * @return 検索結果
   */
  Mono<Cart> findByAccountId(AccountId accountId);

  /**
   * カートを保存します
   *
   * @param cart 保存するカート
   * @return 保存されたカート
   */
  Mono<Cart> save(Cart cart);

  /**
   * カートを削除します
   *
   * @param id カートID
   * @return 完了シグナル
   */
  Mono<Void> deleteById(CartId id);
}
