package com.example.ec_2024b_back.logistics;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

/** 配送IDを表す値オブジェクト */
public record ShipmentId(UUID value) implements Identifier {
  /**
   * 文字列形式のIDからShipmentIdを生成します
   *
   * @param id 文字列形式のID
   * @return 生成されたShipmentId
   */
  public static ShipmentId of(String id) {
    return new ShipmentId(UUID.fromString(id));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
