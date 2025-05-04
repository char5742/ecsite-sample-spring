package com.example.ec_2024b_back.shopping.domain.models;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.OrderId;
import com.example.ec_2024b_back.shopping.PaymentId;
import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.time.Instant;
import org.jmolecules.event.types.DomainEvent;
import org.jspecify.annotations.Nullable;

/** 注文に関するドメインイベントを定義します。 */
public sealed interface OrderEvent extends DomainEvent {

  /**
   * 注文が作成されたイベント
   *
   * @param orderId 注文ID
   * @param accountId アカウントID
   * @param items 注文アイテムリスト
   * @param totalAmount 合計金額
   * @param shippingAddress 配送先住所（国・都道府県・市区町村・番地・建物名）
   * @param occurredAt 発生時刻
   */
  record OrderPlaced(
      OrderId orderId,
      AccountId accountId,
      ImmutableList<OrderItem> items,
      BigDecimal totalAmount,
      String shippingAddress,
      Instant occurredAt)
      implements OrderEvent {}

  /**
   * 注文がキャンセルされたイベント
   *
   * @param orderId 注文ID
   * @param reason キャンセル理由
   * @param occurredAt 発生時刻
   */
  record OrderCancelled(OrderId orderId, String reason, Instant occurredAt) implements OrderEvent {}

  /**
   * 注文の支払いが完了したイベント
   *
   * @param orderId 注文ID
   * @param paymentId 支払いID
   * @param paymentMethod 支払い方法
   * @param amount 支払い金額
   * @param occurredAt 発生時刻
   */
  record OrderPaid(
      OrderId orderId,
      PaymentId paymentId,
      String paymentMethod,
      BigDecimal amount,
      Instant occurredAt)
      implements OrderEvent {}

  /**
   * 注文が出荷されたイベント
   *
   * @param orderId 注文ID
   * @param trackingNumber 追跡番号
   * @param shippingMethod 配送方法
   * @param occurredAt 発生時刻
   */
  record OrderShipped(
      OrderId orderId, @Nullable String trackingNumber, String shippingMethod, Instant occurredAt)
      implements OrderEvent {}

  /**
   * 注文が配送完了したイベント
   *
   * @param orderId 注文ID
   * @param deliveredAt 配送完了日時
   * @param occurredAt 発生時刻
   */
  record OrderDelivered(OrderId orderId, Instant deliveredAt, Instant occurredAt)
      implements OrderEvent {}

  /**
   * 注文が完了したイベント
   *
   * @param orderId 注文ID
   * @param occurredAt 発生時刻
   */
  record OrderCompleted(OrderId orderId, Instant occurredAt) implements OrderEvent {}
}
