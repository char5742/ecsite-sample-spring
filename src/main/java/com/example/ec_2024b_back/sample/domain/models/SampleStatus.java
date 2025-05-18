package com.example.ec_2024b_back.sample.domain.models;

/**
 * サンプルエンティティのステータスを表す列挙型。
 *
 * <p>この列挙型は、エンティティの状態遷移を表現する例を示します。
 */
public enum SampleStatus {
  /** 下書き状態 */
  DRAFT,

  /** アクティブ状態 */
  ACTIVE,

  /** 非アクティブ状態 */
  INACTIVE,

  /** アーカイブ済み */
  ARCHIVED
}
