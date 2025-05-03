package com.example.ec_2024b_back.product;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

/**
 * カテゴリを一意に識別するID
 *
 * @param value IDの値（UUID）
 */
public record CategoryId(UUID value) implements Identifier {
  /**
   * 文字列からCategoryIdを生成します
   *
   * @param id UUID形式の文字列
   * @return 文字列から変換されたCategoryId
   */
  public static CategoryId of(String id) {
    return new CategoryId(UUID.fromString(id));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
