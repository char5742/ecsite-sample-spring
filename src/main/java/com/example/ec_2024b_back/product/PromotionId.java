package com.example.ec_2024b_back.product;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

/**
 * プロモーションを一意に識別するID
 *
 * @param value IDの値（UUID）
 */
public record PromotionId(UUID value) implements Identifier {
  /**
   * 文字列からPromotionIdを生成します
   *
   * @param id UUID形式の文字列
   * @return 文字列から変換されたPromotionId
   */
  public static PromotionId of(String id) {
    return new PromotionId(UUID.fromString(id));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
