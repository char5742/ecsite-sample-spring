package com.example.ec_2024b_back.product;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

/**
 * 在庫を一意に識別するID
 *
 * @param value IDの文字列表現（UUID）
 */
public record InventoryId(UUID value) implements Identifier {
  public UUID getValue() {
    return value;
  }

  public InventoryId {
    if (value == null) {
      throw new IllegalArgumentException("InventoryIdはnullであってはなりません");
    }
  }

  /**
   * ランダムなInventoryIdを生成します
   *
   * @return 新しいInventoryId
   */
  public static InventoryId generate() {
    return new InventoryId(UUID.randomUUID());
  }

  /**
   * 既存のUUIDからInventoryIdを生成します
   *
   * @param uuid 既存のUUID
   * @return UUIDに基づくInventoryId
   */
  public static InventoryId fromUUID(UUID uuid) {
    return new InventoryId(uuid);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
