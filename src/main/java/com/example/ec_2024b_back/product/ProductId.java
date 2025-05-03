package com.example.ec_2024b_back.product;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

/**
 * 商品を一意に識別するID
 *
 * @param value IDの文字列表現（UUID）
 */
public record ProductId(UUID value) implements Identifier {
  public UUID getValue() {
    return value;
  }

  public ProductId {
    if (value == null) {
      throw new IllegalArgumentException("ProductIdはnullであってはなりません");
    }
  }

  /**
   * ランダムなProductIdを生成します
   *
   * @return 新しいProductId
   */
  public static ProductId generate() {
    return new ProductId(UUID.randomUUID());
  }

  /**
   * 既存のUUIDからProductIdを生成します
   *
   * @param uuid 既存のUUID
   * @return UUIDに基づくProductId
   */
  public static ProductId fromUUID(UUID uuid) {
    return new ProductId(uuid);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
