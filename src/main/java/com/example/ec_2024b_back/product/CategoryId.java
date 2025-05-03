package com.example.ec_2024b_back.product;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

/**
 * カテゴリを一意に識別するID
 *
 * @param value IDの文字列表現（UUID）
 */
public record CategoryId(UUID value) implements Identifier {
  public UUID getValue() {
    return value;
  }

  public CategoryId {
    if (value == null) {
      throw new IllegalArgumentException("CategoryIdはnullであってはなりません");
    }
  }

  /**
   * ランダムなCategoryIdを生成します
   *
   * @return 新しいCategoryId
   */
  public static CategoryId generate() {
    return new CategoryId(UUID.randomUUID());
  }

  /**
   * 既存のUUIDからCategoryIdを生成します
   *
   * @param uuid 既存のUUID
   * @return UUIDに基づくCategoryId
   */
  public static CategoryId fromUUID(UUID uuid) {
    return new CategoryId(uuid);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
