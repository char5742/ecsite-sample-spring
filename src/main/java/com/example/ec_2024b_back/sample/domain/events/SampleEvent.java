package com.example.ec_2024b_back.sample.domain.events;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.sample.domain.models.SampleStatus;
import java.time.Instant;
import org.jmolecules.event.types.DomainEvent;
import org.jspecify.annotations.Nullable;

/**
 * サンプルエンティティに関するドメインイベント。
 *
 * <p>このインターフェースは、ドメインイベントの実装例を示します。 sealed interfaceを使用して、イベントタイプを限定し、型安全性を確保しています。
 */
public sealed interface SampleEvent extends DomainEvent {

  /**
   * サンプルが作成されたことを表すイベント。
   *
   * @param sampleId サンプルID
   * @param name 名前
   * @param description 説明（nullable）
   * @param occurredAt 発生時刻
   */
  record SampleCreated(
      SampleId sampleId, String name, @Nullable String description, Instant occurredAt)
      implements SampleEvent {}

  /**
   * サンプルの名前が更新されたことを表すイベント。
   *
   * @param sampleId サンプルID
   * @param oldName 旧名前
   * @param newName 新名前
   * @param occurredAt 発生時刻
   */
  record SampleNameUpdated(SampleId sampleId, String oldName, String newName, Instant occurredAt)
      implements SampleEvent {}

  /**
   * サンプルの説明が更新されたことを表すイベント。
   *
   * @param sampleId サンプルID
   * @param oldDescription 旧説明（nullable）
   * @param newDescription 新説明（nullable）
   * @param occurredAt 発生時刻
   */
  record SampleDescriptionUpdated(
      SampleId sampleId,
      @Nullable String oldDescription,
      @Nullable String newDescription,
      Instant occurredAt)
      implements SampleEvent {}

  /**
   * サンプルのステータスが変更されたことを表すイベント。
   *
   * @param sampleId サンプルID
   * @param oldStatus 旧ステータス
   * @param newStatus 新ステータス
   * @param occurredAt 発生時刻
   */
  record SampleStatusChanged(
      SampleId sampleId, SampleStatus oldStatus, SampleStatus newStatus, Instant occurredAt)
      implements SampleEvent {}

  /**
   * サンプルが削除されたことを表すイベント。
   *
   * @param sampleId サンプルID
   * @param occurredAt 発生時刻
   */
  record SampleDeleted(SampleId sampleId, Instant occurredAt) implements SampleEvent {}
}
