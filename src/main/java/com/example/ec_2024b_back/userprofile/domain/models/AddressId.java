package com.example.ec_2024b_back.userprofile.domain.models;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

/**
 * 住所を一意に識別するID
 *
 * @param id IDの値（UUID）
 */
public record AddressId(UUID id) implements Identifier {
  /**
   * 文字列からAddressIdを生成します
   *
   * @param id UUID形式の文字列
   * @return 文字列から変換されたAddressId
   */
  public static AddressId of(String id) {
    return new AddressId(UUID.fromString(id));
  }

  @Override
  public String toString() {
    return id.toString();
  }
}
