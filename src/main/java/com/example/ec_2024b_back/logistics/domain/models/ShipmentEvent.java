package com.example.ec_2024b_back.logistics.domain.models;

import com.example.ec_2024b_back.logistics.ShipmentId;
import com.example.ec_2024b_back.shopping.domain.models.OrderId;
import java.time.Instant;
import org.jmolecules.event.types.DomainEvent;
import org.jspecify.annotations.Nullable;

/** 配送に関するドメインイベントを定義します。 */
public sealed interface ShipmentEvent extends DomainEvent {

  /**
   * 配送が作成されたイベント
   *
   * @param shipmentId 配送ID
   * @param orderId 注文ID
   * @param shippingAddress 配送先住所
   * @param shippingMethod 配送方法
   * @param estimatedDeliveryDate 配送予定日
   * @param occurredAt 発生時刻
   */
  record ShipmentCreated(
      ShipmentId shipmentId,
      OrderId orderId,
      String shippingAddress,
      String shippingMethod,
      @Nullable Instant estimatedDeliveryDate,
      Instant occurredAt)
      implements ShipmentEvent {}

  /**
   * 配送ステータスが更新されたイベント
   *
   * @param shipmentId 配送ID
   * @param previousStatus 前のステータス
   * @param currentStatus 現在のステータス
   * @param trackingNumber 追跡番号
   * @param note 備考
   * @param occurredAt 発生時刻
   */
  record ShipmentStatusUpdated(
      ShipmentId shipmentId,
      ShipmentStatus previousStatus,
      ShipmentStatus currentStatus,
      @Nullable String trackingNumber,
      @Nullable String note,
      Instant occurredAt)
      implements ShipmentEvent {}

  /**
   * 配送が到着したイベント
   *
   * @param shipmentId 配送ID
   * @param trackingNumber 追跡番号
   * @param arrivedAt 到着時刻
   * @param occurredAt 発生時刻
   */
  record ShipmentArrived(
      ShipmentId shipmentId, @Nullable String trackingNumber, Instant arrivedAt, Instant occurredAt)
      implements ShipmentEvent {}

  /**
   * 配送が完了したイベント
   *
   * @param shipmentId 配送ID
   * @param receiverName 受取人名
   * @param deliveredAt 配送完了時刻
   * @param occurredAt 発生時刻
   */
  record ShipmentDelivered(
      ShipmentId shipmentId, @Nullable String receiverName, Instant deliveredAt, Instant occurredAt)
      implements ShipmentEvent {}

  /**
   * 配送が返送されたイベント
   *
   * @param shipmentId 配送ID
   * @param reason 返送理由
   * @param returnedAt 返送時刻
   * @param occurredAt 発生時刻
   */
  record ShipmentReturned(
      ShipmentId shipmentId, String reason, Instant returnedAt, Instant occurredAt)
      implements ShipmentEvent {}
}
