package com.example.ec_2024b_back.sample.domain.models;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.share.domain.models.AuditInfo;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * サンプルエンティティ。
 *
 * <p>このクラスは、DDDにおけるエンティティの実装例を示します。 エンティティは識別子（ID）を持ち、ライフサイクルを通じて変化する可能性があります。
 */
@Getter
@ToString
public class Sample {
  private final SampleId id;
  private String name;
  private @Nullable String description;
  private SampleStatus status;
  private final AuditInfo auditInfo;

  /**
   * Sampleエンティティを作成します。
   *
   * @param id サンプルID（非null）
   * @param name 名前（非null）
   * @param description 説明（nullable）
   * @param status ステータス（非null）
   * @param auditInfo 監査情報（非null）
   */
  public Sample(
      SampleId id,
      String name,
      @Nullable String description,
      SampleStatus status,
      AuditInfo auditInfo) {
    if (id == null) {
      throw new IllegalArgumentException("IDは必須です");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("名前は必須です");
    }
    if (status == null) {
      throw new IllegalArgumentException("ステータスは必須です");
    }
    if (auditInfo == null) {
      throw new IllegalArgumentException("監査情報は必須です");
    }

    this.id = id;
    this.name = name;
    this.description = description;
    this.status = status;
    this.auditInfo = auditInfo;
  }

  /**
   * 名前を更新します。
   *
   * @param name 新しい名前（非null）
   */
  public void updateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("名前は必須です");
    }
    this.name = name;
  }

  /**
   * 説明を更新します。
   *
   * @param description 新しい説明（nullable）
   */
  public void updateDescription(@Nullable String description) {
    this.description = description;
  }

  /**
   * ステータスを更新します。
   *
   * @param status 新しいステータス（非null）
   */
  public void updateStatus(SampleStatus status) {
    if (status == null) {
      throw new IllegalArgumentException("ステータスは必須です");
    }
    this.status = status;
  }

  /**
   * エンティティがアクティブ状態かどうかを判定します。
   *
   * @return アクティブ状態の場合true
   */
  public boolean isActive() {
    return status == SampleStatus.ACTIVE;
  }
}
