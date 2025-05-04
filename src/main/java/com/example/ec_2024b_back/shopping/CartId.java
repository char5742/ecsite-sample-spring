package com.example.ec_2024b_back.shopping;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

/** カートを一意に識別するID値オブジェクト */
public record CartId(UUID id) implements Identifier {

  /**
   * 文字列形式のIDからCartIdを生成します
   *
   * @param id ID文字列
   * @return カートID
   */
  public static CartId of(String id) {
    return new CartId(UUID.fromString(id));
  }

  /**
   * 新しいカートIDを生成します
   *
   * @return 新しいカートID
   */
  public static CartId generate() {
    return new CartId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return id.toString();
  }
}
