package com.example.ec_2024b_back.product;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

/**
 * プロモーションを一意に識別するID
 *
 * @param value IDの文字列表現（UUID）
 */
public record PromotionId(UUID value) implements Identifier {
  public UUID getValue() {
    return value;
  }

  public PromotionId {
    if (value == null) {
      throw new IllegalArgumentException("PromotionIdはnullであってはなりません");
    }
  }

  /**
   * ランダムなPromotionIdを生成します
   *
   * @return 新しいPromotionId
   */
  public static PromotionId generate() {
    return new PromotionId(UUID.randomUUID());
  }

  /**
   * 既存のUUIDからPromotionIdを生成します
   *
   * @param uuid 既存のUUID
   * @return UUIDに基づくPromotionId
   */
  public static PromotionId fromUUID(UUID uuid) {
    return new PromotionId(uuid);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
