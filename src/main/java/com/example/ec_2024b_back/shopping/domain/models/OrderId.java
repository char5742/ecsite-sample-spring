package com.example.ec_2024b_back.shopping.domain.models;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

/** 注文を一意に識別するID値オブジェクト */
public record OrderId(UUID id) implements Identifier {

  /**
   * 文字列形式のIDからOrderIdを生成します
   *
   * @param id ID文字列
   * @return 注文ID
   */
  public static OrderId of(String id) {
    return new OrderId(UUID.fromString(id));
  }

  /**
   * 新しい注文IDを生成します
   *
   * @return 新しい注文ID
   */
  public static OrderId generate() {
    return new OrderId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return id.toString();
  }
}
