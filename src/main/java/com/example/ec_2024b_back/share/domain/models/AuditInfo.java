package com.example.ec_2024b_back.share.domain.models;

import java.time.Instant;

/** 監査情報を表す値オブジェクト 作成日時、更新日時などの監査に関する情報を保持します */
public record AuditInfo(Instant createdAt, Instant updatedAt) {

  /**
   * 新しい監査情報を作成します
   *
   * @param now 現在時刻
   * @return 作成された監査情報
   */
  public static AuditInfo create(Instant now) {
    return new AuditInfo(now, now);
  }

  /**
   * 監査情報を更新します
   *
   * @param now 現在時刻
   * @return 更新された監査情報
   */
  public AuditInfo update(Instant now) {
    return new AuditInfo(this.createdAt, now);
  }

  /**
   * データストアから監査情報を復元します
   *
   * @param createdAt 作成日時
   * @param updatedAt 更新日時
   * @return 復元された監査情報
   */
  public static AuditInfo reconstruct(Instant createdAt, Instant updatedAt) {
    return new AuditInfo(createdAt, updatedAt);
  }
}
