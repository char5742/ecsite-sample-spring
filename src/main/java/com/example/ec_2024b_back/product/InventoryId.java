package com.example.ec_2024b_back.product;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

/**
 * 在庫を一意に識別するID
 *
 * @param value IDの値（UUID）
 */
public record InventoryId(UUID value) implements Identifier {
  /**
   * 文字列からInventoryIdを生成します
   *
   * @param id UUID形式の文字列
   * @return 文字列から変換されたInventoryId
   */
  public static InventoryId of(String id) {
    return new InventoryId(UUID.fromString(id));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
