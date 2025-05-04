package com.example.ec_2024b_back.shopping.domain.models;

import com.example.ec_2024b_back.product.ProductId;
import java.math.BigDecimal;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/** カート内の商品アイテムを表す値オブジェクト */
public record CartItem(
    ProductId productId, String productName, int quantity, BigDecimal unitPrice) {

  /** カートアイテムの検証を行うコンストラクタ */
  public CartItem {
    Objects.requireNonNull(productId, "商品IDは必須です");
    Objects.requireNonNull(productName, "商品名は必須です");
    Objects.requireNonNull(unitPrice, "単価は必須です");

    if (productName.isBlank()) {
      throw new IllegalArgumentException("商品名は空白であってはなりません");
    }
    if (quantity <= 0) {
      throw new IllegalArgumentException("数量は1以上でなければなりません");
    }
    if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("単価は0以上でなければなりません");
    }
  }

  /**
   * 数量を変更した新しいカートアイテムを作成します
   *
   * @param newQuantity 新しい数量
   * @return 更新されたカートアイテム
   */
  public CartItem withQuantity(int newQuantity) {
    return new CartItem(this.productId, this.productName, newQuantity, this.unitPrice);
  }

  /**
   * このアイテムの合計金額を計算します
   *
   * @return 合計金額
   */
  public BigDecimal calculateTotal() {
    return unitPrice.multiply(BigDecimal.valueOf(quantity));
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CartItem cartItem)) {
      return false;
    }

    return quantity == cartItem.quantity
        && Objects.equals(productId, cartItem.productId)
        && Objects.equals(productName, cartItem.productName)
        && unitPrice.compareTo(cartItem.unitPrice) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(productId, productName, quantity, unitPrice);
  }
}
