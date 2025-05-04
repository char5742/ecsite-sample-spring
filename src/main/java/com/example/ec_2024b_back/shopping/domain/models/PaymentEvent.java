package com.example.ec_2024b_back.shopping.domain.models;

import com.example.ec_2024b_back.shopping.OrderId;
import com.example.ec_2024b_back.shopping.PaymentId;
import java.math.BigDecimal;
import java.time.Instant;
import org.jmolecules.event.types.DomainEvent;
import org.jspecify.annotations.Nullable;

/** 支払いに関するドメインイベントを定義します。 */
public sealed interface PaymentEvent extends DomainEvent {

  /**
   * 支払いが承認されたイベント
   *
   * @param paymentId 支払いID
   * @param orderId 注文ID
   * @param amount 金額
   * @param paymentMethod 支払い方法
   * @param authorizedAt 承認日時
   * @param externalTransactionId 外部トランザクションID
   * @param occurredAt 発生時刻
   */
  record PaymentAuthorized(
      PaymentId paymentId,
      OrderId orderId,
      BigDecimal amount,
      String paymentMethod,
      Instant authorizedAt,
      @Nullable String externalTransactionId,
      Instant occurredAt)
      implements PaymentEvent {}

  /**
   * 支払いが確定したイベント
   *
   * @param paymentId 支払いID
   * @param orderId 注文ID
   * @param amount 金額
   * @param externalTransactionId 外部トランザクションID
   * @param occurredAt 発生時刻
   */
  record PaymentCaptured(
      PaymentId paymentId,
      OrderId orderId,
      BigDecimal amount,
      @Nullable String externalTransactionId,
      Instant occurredAt)
      implements PaymentEvent {}

  /**
   * 支払いが失敗したイベント
   *
   * @param paymentId 支払いID
   * @param orderId 注文ID
   * @param amount 金額
   * @param errorCode エラーコード
   * @param errorMessage エラーメッセージ
   * @param occurredAt 発生時刻
   */
  record PaymentFailed(
      PaymentId paymentId,
      OrderId orderId,
      BigDecimal amount,
      @Nullable String errorCode,
      String errorMessage,
      Instant occurredAt)
      implements PaymentEvent {}

  /**
   * 支払いが返金されたイベント
   *
   * @param paymentId 支払いID
   * @param orderId 注文ID
   * @param amount 返金金額
   * @param reason 返金理由
   * @param externalTransactionId 外部トランザクションID
   * @param occurredAt 発生時刻
   */
  record PaymentRefunded(
      PaymentId paymentId,
      OrderId orderId,
      BigDecimal amount,
      String reason,
      @Nullable String externalTransactionId,
      Instant occurredAt)
      implements PaymentEvent {}
}
