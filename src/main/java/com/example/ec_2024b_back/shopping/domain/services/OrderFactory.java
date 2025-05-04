package com.example.ec_2024b_back.shopping.domain.services;

import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import com.example.ec_2024b_back.shopping.domain.models.Order;
import com.example.ec_2024b_back.shopping.domain.models.OrderId;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;

/** 注文オブジェクトを生成するファクトリークラス */
@RequiredArgsConstructor
public class OrderFactory {
  private final IdGenerator idGenerator;
  private final Clock clock;

  /**
   * カートから新しい注文を作成します
   *
   * @param cart カート
   * @param shippingAddress 配送先住所
   * @param shippingCost 配送料
   * @param taxRate 税率（例: 0.1 = 10%）
   * @return 作成された注文
   */
  public Order createFromCart(
      Cart cart, String shippingAddress, BigDecimal shippingCost, BigDecimal taxRate) {

    var orderId = new OrderId(idGenerator.newId());
    var now = Instant.now(clock);

    return Order.createFromCart(orderId, cart, shippingAddress, shippingCost, taxRate, now);
  }
}
