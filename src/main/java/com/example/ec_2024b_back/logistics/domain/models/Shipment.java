package com.example.ec_2024b_back.logistics.domain.models;

import com.example.ec_2024b_back.logistics.ShipmentId;
import com.example.ec_2024b_back.logistics.domain.models.ShipmentEvent.ShipmentArrived;
import com.example.ec_2024b_back.logistics.domain.models.ShipmentEvent.ShipmentCreated;
import com.example.ec_2024b_back.logistics.domain.models.ShipmentEvent.ShipmentDelivered;
import com.example.ec_2024b_back.logistics.domain.models.ShipmentEvent.ShipmentReturned;
import com.example.ec_2024b_back.logistics.domain.models.ShipmentEvent.ShipmentStatusUpdated;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.share.domain.models.AuditInfo;
import com.example.ec_2024b_back.shopping.OrderId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jspecify.annotations.Nullable;

/** 配送情報を表す集約ルート */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Shipment implements AggregateRoot<Shipment, ShipmentId> {
  private final ShipmentId id;
  private final OrderId orderId;
  private final String shippingAddress;
  private final String shippingMethod;
  private final ShipmentStatus status;
  private final @Nullable String trackingNumber;
  private final @Nullable String note;
  private final @Nullable Instant estimatedDeliveryDate;
  private final @Nullable Instant actualDeliveryDate;
  private final @Nullable String receiverName;
  private final List<ShipmentEvent> events;
  private final AuditInfo auditInfo;

  /**
   * 注文情報から新しい配送情報を作成します
   *
   * @param id 配送ID
   * @param orderId 注文参照ID
   * @param shippingAddress 配送先住所
   * @param shippingMethod 配送方法
   * @param estimatedDeliveryDate 配送予定日時
   * @param now 現在時刻
   * @return 作成された配送情報
   */
  public static Shipment create(
      ShipmentId id,
      OrderId orderId,
      String shippingAddress,
      String shippingMethod,
      @Nullable Instant estimatedDeliveryDate,
      Instant now) {

    if (shippingAddress.isBlank()) {
      throw new IllegalArgumentException("配送先住所は空白であってはなりません");
    }

    if (shippingMethod.isBlank()) {
      throw new IllegalArgumentException("配送方法は空白であってはなりません");
    }

    var events = new ArrayList<ShipmentEvent>();
    events.add(
        new ShipmentCreated(
            id, orderId, shippingAddress, shippingMethod, estimatedDeliveryDate, now));

    return new Shipment(
        id,
        orderId,
        shippingAddress,
        shippingMethod,
        ShipmentStatus.CREATED,
        null,
        null,
        estimatedDeliveryDate,
        null,
        null,
        Collections.unmodifiableList(events),
        AuditInfo.create(now));
  }

  /**
   * データストアから配送情報を復元します
   *
   * @param id 配送ID
   * @param orderId 注文参照ID
   * @param shippingAddress 配送先住所
   * @param shippingMethod 配送方法
   * @param status 配送状態
   * @param trackingNumber 追跡番号
   * @param note 備考
   * @param estimatedDeliveryDate 配送予定日時
   * @param actualDeliveryDate 実際の配送完了日時
   * @param receiverName 受取人名
   * @param auditInfo 監査情報
   * @return 復元された配送情報
   */
  @SuppressWarnings("TooManyParameters")
  public static Shipment reconstruct(
      ShipmentId id,
      OrderId orderId,
      String shippingAddress,
      String shippingMethod,
      ShipmentStatus status,
      @Nullable String trackingNumber,
      @Nullable String note,
      @Nullable Instant estimatedDeliveryDate,
      @Nullable Instant actualDeliveryDate,
      @Nullable String receiverName,
      AuditInfo auditInfo) {
    return new Shipment(
        id,
        orderId,
        shippingAddress,
        shippingMethod,
        status,
        trackingNumber,
        note,
        estimatedDeliveryDate,
        actualDeliveryDate,
        receiverName,
        Collections.emptyList(), // 復元時は空のイベントリスト
        auditInfo);
  }

  /**
   * 配送状態を更新します
   *
   * @param newStatus 新しい配送状態
   * @param trackingNumber 追跡番号（オプション）
   * @param note 備考（オプション）
   * @param now 現在時刻
   * @return 更新された配送情報
   * @throws DomainException 配送状態の更新ができない場合
   */
  public Shipment updateStatus(
      ShipmentStatus newStatus,
      @Nullable String trackingNumber,
      @Nullable String note,
      Instant now) {
    if (!status.canTransitionTo(newStatus)) {
      throw new DomainException("現在の状態 " + status + " からは " + newStatus + " に変更できません");
    }

    var newEvents = new ArrayList<>(this.events);
    newEvents.add(new ShipmentStatusUpdated(id, status, newStatus, trackingNumber, note, now));

    return new Shipment(
        id,
        orderId,
        shippingAddress,
        shippingMethod,
        newStatus,
        trackingNumber != null ? trackingNumber : this.trackingNumber,
        note != null ? note : this.note,
        estimatedDeliveryDate,
        actualDeliveryDate,
        receiverName,
        Collections.unmodifiableList(newEvents),
        auditInfo.update(now));
  }

  /**
   * 配送が到着したことを記録します
   *
   * @param arrivedAt 到着時刻
   * @param now 現在時刻
   * @return 更新された配送情報
   * @throws DomainException 配送状態の更新ができない場合
   */
  public Shipment markArrived(Instant arrivedAt, Instant now) {
    if (!status.canTransitionTo(ShipmentStatus.ARRIVED)) {
      throw new DomainException("現在の状態 " + status + " からは ARRIVED に変更できません");
    }

    var newEvents = new ArrayList<>(this.events);
    newEvents.add(new ShipmentArrived(id, trackingNumber, arrivedAt, now));

    return new Shipment(
        id,
        orderId,
        shippingAddress,
        shippingMethod,
        ShipmentStatus.ARRIVED,
        trackingNumber,
        note,
        estimatedDeliveryDate,
        null,
        receiverName,
        Collections.unmodifiableList(newEvents),
        auditInfo.update(now));
  }

  /**
   * 配送が完了したことを記録します
   *
   * @param deliveredAt 配送完了時刻
   * @param receiverName 受取人名（オプション）
   * @param now 現在時刻
   * @return 更新された配送情報
   * @throws DomainException 配送状態の更新ができない場合
   */
  public Shipment markDelivered(Instant deliveredAt, @Nullable String receiverName, Instant now) {
    if (!status.canTransitionTo(ShipmentStatus.DELIVERED)) {
      throw new DomainException("現在の状態 " + status + " からは DELIVERED に変更できません");
    }

    var newEvents = new ArrayList<>(this.events);
    newEvents.add(new ShipmentDelivered(id, receiverName, deliveredAt, now));

    return new Shipment(
        id,
        orderId,
        shippingAddress,
        shippingMethod,
        ShipmentStatus.DELIVERED,
        trackingNumber,
        note,
        estimatedDeliveryDate,
        deliveredAt,
        receiverName != null ? receiverName : this.receiverName,
        Collections.unmodifiableList(newEvents),
        auditInfo.update(now));
  }

  /**
   * 配送が返送されたことを記録します
   *
   * @param reason 返送理由
   * @param returnedAt 返送時刻
   * @param now 現在時刻
   * @return 更新された配送情報
   * @throws DomainException 配送状態の更新ができない場合
   */
  public Shipment markReturned(String reason, Instant returnedAt, Instant now) {
    if (reason.isBlank()) {
      throw new IllegalArgumentException("返送理由は空白であってはなりません");
    }

    if (!status.canTransitionTo(ShipmentStatus.RETURNED)) {
      throw new DomainException("現在の状態 " + status + " からは RETURNED に変更できません");
    }

    var newEvents = new ArrayList<>(this.events);
    newEvents.add(new ShipmentReturned(id, reason, returnedAt, now));

    return new Shipment(
        id,
        orderId,
        shippingAddress,
        shippingMethod,
        ShipmentStatus.RETURNED,
        trackingNumber,
        note,
        estimatedDeliveryDate,
        null,
        receiverName,
        Collections.unmodifiableList(newEvents),
        auditInfo.update(now));
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Shipment shipment)) {
      return false;
    }

    return Objects.equals(id, shipment.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
