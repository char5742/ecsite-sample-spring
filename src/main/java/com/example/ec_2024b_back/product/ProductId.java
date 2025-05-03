package com.example.ec_2024b_back.product;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

/**
 * 商品を一意に識別するID
 *
 * @param value IDの値（UUID）
 */
public record ProductId(UUID value) implements Identifier {
  /**
   * 文字列からProductIdを生成します
   *
   * @param id UUID形式の文字列
   * @return 文字列から変換されたProductId
   */
  public static ProductId of(String id) {
    return new ProductId(UUID.fromString(id));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
