package com.example.ec_2024b_back.shopping.domain.models;

import com.example.ec_2024b_back.product.ProductId;
import java.math.BigDecimal;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/** 注文内の商品アイテムを表す値オブジェクト */
public record OrderItem(
    ProductId productId,
    String productName,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal) {

  /** 注文アイテムの検証を行うコンストラクタ */
  public OrderItem {
    if (productName.isBlank()) {
      throw new IllegalArgumentException("商品名は空白であってはなりません");
    }
    if (quantity <= 0) {
      throw new IllegalArgumentException("数量は1以上でなければなりません");
    }
    if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("単価は0以上でなければなりません");
    }
    if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("小計は0以上でなければなりません");
    }

    // 小計が正しいことを検証
    var calculatedSubtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    if (subtotal.compareTo(calculatedSubtotal) != 0) {
      throw new IllegalArgumentException("小計が一致しません: " + subtotal + " != " + calculatedSubtotal);
    }
  }

  /**
   * カートアイテムから注文アイテムを作成します
   *
   * @param cartItem カートアイテム
   * @return 作成された注文アイテム
   */
  public static OrderItem fromCartItem(CartItem cartItem) {
    var subtotal = cartItem.unitPrice().multiply(BigDecimal.valueOf(cartItem.quantity()));
    return new OrderItem(
        cartItem.productId(),
        cartItem.productName(),
        cartItem.quantity(),
        cartItem.unitPrice(),
        subtotal);
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof OrderItem orderItem)) {
      return false;
    }

    return quantity == orderItem.quantity
        && Objects.equals(productId, orderItem.productId)
        && Objects.equals(productName, orderItem.productName)
        && unitPrice.compareTo(orderItem.unitPrice) == 0
        && subtotal.compareTo(orderItem.subtotal) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(productId, productName, quantity, unitPrice, subtotal);
  }
}
