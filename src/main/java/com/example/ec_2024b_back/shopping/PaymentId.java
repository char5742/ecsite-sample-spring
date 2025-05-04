package com.example.ec_2024b_back.shopping;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

/** 支払いを一意に識別するID値オブジェクト */
public record PaymentId(UUID id) implements Identifier {

  /**
   * 文字列形式のIDからPaymentIdを生成します
   *
   * @param id ID文字列
   * @return 支払いID
   */
  public static PaymentId of(String id) {
    return new PaymentId(UUID.fromString(id));
  }

  /**
   * 新しい支払いIDを生成します
   *
   * @return 新しい支払いID
   */
  public static PaymentId generate() {
    return new PaymentId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return id.toString();
  }
}
