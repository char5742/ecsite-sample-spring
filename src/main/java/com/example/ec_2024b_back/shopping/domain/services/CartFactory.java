package com.example.ec_2024b_back.shopping.domain.services;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import com.example.ec_2024b_back.shopping.domain.models.CartId;
import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import lombok.RequiredArgsConstructor;

/** カートオブジェクトを生成するファクトリークラス */
@RequiredArgsConstructor
public class CartFactory {
  private final IdGenerator idGenerator;
  private final Clock clock;

  /**
   * 新しい空のカートを作成します
   *
   * @param accountId アカウントID
   * @return 新しいカート
   */
  public Cart createEmptyCart(AccountId accountId) {
    var cartId = new CartId(idGenerator.newId());
    var now = Instant.now(clock);

    return Cart.reconstruct(cartId, accountId, Collections.emptyList(), now, now);
  }
}
