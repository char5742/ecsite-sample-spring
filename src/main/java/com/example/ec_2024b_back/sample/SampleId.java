package com.example.ec_2024b_back.sample;

import java.util.UUID;

/**
 * サンプルエンティティの識別子を表す値オブジェクト。
 *
 * <p>このクラスは、エンティティ識別子の実装例を示します。 値オブジェクトとして不変性を保証し、recordを使用して実装されています。
 *
 * @param value 内部で保持するUUID値（非null）
 */
public record SampleId(UUID value) {
  /**
   * 新しいサンプルIDを生成します。
   *
   * @param value UUID値（非null）
   */
  public SampleId {}

  /**
   * 文字列表現を返します。
   *
   * @return UUID値の文字列表現
   */
  @Override
  public String toString() {
    return value.toString();
  }
}
