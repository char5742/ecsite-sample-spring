package com.example.ec_2024b_back.shopping.domain.models;

import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.share.domain.models.AuditInfo;
import com.example.ec_2024b_back.shopping.domain.models.PaymentEvent.PaymentAuthorized;
import com.example.ec_2024b_back.shopping.domain.models.PaymentEvent.PaymentCaptured;
import com.example.ec_2024b_back.shopping.domain.models.PaymentEvent.PaymentFailed;
import com.example.ec_2024b_back.shopping.domain.models.PaymentEvent.PaymentRefunded;
import java.math.BigDecimal;
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

/** 支払いを表す集約ルート */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Payment implements AggregateRoot<Payment, PaymentId> {

  private final PaymentId id;
  private final OrderId orderId;
  private final BigDecimal amount;
  private final PaymentStatus status;
  private final String paymentMethod;
  private final @Nullable String externalTransactionId;
  private final PaymentError error;
  private final List<PaymentEvent> events;
  private final AuditInfo auditInfo;

  /**
   * 新しい支払いを開始します
   *
   * @param id 支払いID
   * @param orderId 注文ID
   * @param amount 金額
   * @param paymentMethod 支払い方法
   * @param now 現在時刻
   * @return 作成された支払い
   */
  public static Payment initiate(
      PaymentId id, OrderId orderId, BigDecimal amount, String paymentMethod, Instant now) {

    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("金額は0より大きい値でなければなりません");
    }

    if (paymentMethod.isBlank()) {
      throw new IllegalArgumentException("支払い方法は空白であってはなりません");
    }

    return new Payment(
        id,
        orderId,
        amount,
        PaymentStatus.PENDING,
        paymentMethod,
        null,
        PaymentError.empty(),
        Collections.emptyList(),
        AuditInfo.create(now));
  }

  /**
   * データストアから支払いを復元します
   *
   * @param id 支払いID
   * @param orderId 注文ID
   * @param amount 金額
   * @param status 支払い状態
   * @param paymentMethod 支払い方法
   * @param externalTransactionId 外部トランザクションID
   * @param errorCode エラーコード
   * @param errorMessage エラーメッセージ
   * @param createdAt 作成日時
   * @param updatedAt 更新日時
   * @return 復元された支払い
   */
  @SuppressWarnings("TooManyParameters")
  public static Payment reconstruct(
      PaymentId id,
      OrderId orderId,
      BigDecimal amount,
      PaymentStatus status,
      String paymentMethod,
      @Nullable String externalTransactionId,
      @Nullable String errorCode,
      @Nullable String errorMessage,
      Instant createdAt,
      Instant updatedAt) {
    return new Payment(
        id,
        orderId,
        amount,
        status,
        paymentMethod,
        externalTransactionId,
        PaymentError.reconstruct(errorCode, errorMessage),
        Collections.emptyList(), // 復元時は空のイベントリスト
        AuditInfo.reconstruct(createdAt, updatedAt));
  }

  /**
   * 支払いを承認済み状態に更新します
   *
   * @param externalTransactionId 外部トランザクションID
   * @param authorizedAt 承認日時
   * @param now 現在時刻
   * @return 更新された支払い
   * @throws DomainException 支払いの状態更新ができない場合
   */
  public Payment authorize(
      @Nullable String externalTransactionId, Instant authorizedAt, Instant now) {

    if (!status.canTransitionTo(PaymentStatus.AUTHORIZED)) {
      throw new DomainException("現在の状態 " + status + " の支払いは承認できません");
    }

    var newEvents = new ArrayList<>(this.events);
    newEvents.add(
        new PaymentAuthorized(
            id, orderId, amount, paymentMethod, authorizedAt, externalTransactionId, now));

    return new Payment(
        id,
        orderId,
        amount,
        PaymentStatus.AUTHORIZED,
        paymentMethod,
        externalTransactionId,
        error,
        Collections.unmodifiableList(newEvents),
        auditInfo.update(now));
  }

  /**
   * 支払いを確定済み状態に更新します
   *
   * @param externalTransactionId 外部トランザクションID
   * @param now 現在時刻
   * @return 更新された支払い
   * @throws DomainException 支払いの状態更新ができない場合
   */
  public Payment capture(@Nullable String externalTransactionId, Instant now) {
    if (!status.canTransitionTo(PaymentStatus.CAPTURED)) {
      throw new DomainException("現在の状態 " + status + " の支払いは確定できません");
    }

    var newEvents = new ArrayList<>(this.events);
    newEvents.add(new PaymentCaptured(id, orderId, amount, externalTransactionId, now));

    return new Payment(
        id,
        orderId,
        amount,
        PaymentStatus.CAPTURED,
        paymentMethod,
        externalTransactionId,
        error,
        Collections.unmodifiableList(newEvents),
        auditInfo.update(now));
  }

  /**
   * 支払いを失敗状態に更新します
   *
   * @param errorCode エラーコード
   * @param errorMessage エラーメッセージ
   * @param now 現在時刻
   * @return 更新された支払い
   * @throws DomainException 支払いの状態更新ができない場合
   */
  public Payment fail(@Nullable String errorCode, String errorMessage, Instant now) {
    if (!status.canTransitionTo(PaymentStatus.FAILED)) {
      throw new DomainException("現在の状態 " + status + " の支払いは失敗状態にできません");
    }

    var newError = PaymentError.create(errorCode, errorMessage);

    var newEvents = new ArrayList<>(this.events);
    newEvents.add(new PaymentFailed(id, orderId, amount, errorCode, errorMessage, now));

    return new Payment(
        id,
        orderId,
        amount,
        PaymentStatus.FAILED,
        paymentMethod,
        externalTransactionId,
        newError,
        Collections.unmodifiableList(newEvents),
        auditInfo.update(now));
  }

  /**
   * 支払いを返金状態に更新します
   *
   * @param refundAmount 返金金額
   * @param reason 返金理由
   * @param externalTransactionId 外部トランザクションID
   * @param now 現在時刻
   * @return 更新された支払い
   * @throws DomainException 支払いの状態更新ができない場合
   */
  public Payment refund(
      BigDecimal refundAmount, String reason, @Nullable String externalTransactionId, Instant now) {

    if (reason.isBlank()) {
      throw new IllegalArgumentException("返金理由は空白であってはなりません");
    }

    // 返金金額のチェック
    if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("返金金額は0より大きい値でなければなりません");
    }
    if (refundAmount.compareTo(amount) > 0) {
      throw new IllegalArgumentException("返金金額は支払い金額を超えることはできません");
    }

    PaymentStatus newStatus;
    if (refundAmount.compareTo(amount) == 0) {
      // 全額返金
      if (!status.canTransitionTo(PaymentStatus.REFUNDED)) {
        throw new DomainException("現在の状態 " + status + " の支払いは返金できません");
      }
      newStatus = PaymentStatus.REFUNDED;
    } else {
      // 一部返金
      if (!status.canTransitionTo(PaymentStatus.PARTIALLY_REFUNDED)) {
        throw new DomainException("現在の状態 " + status + " の支払いは部分返金できません");
      }
      newStatus = PaymentStatus.PARTIALLY_REFUNDED;
    }

    var newEvents = new ArrayList<>(this.events);
    newEvents.add(
        new PaymentRefunded(id, orderId, refundAmount, reason, externalTransactionId, now));

    return new Payment(
        id,
        orderId,
        amount,
        newStatus,
        paymentMethod,
        externalTransactionId,
        error,
        Collections.unmodifiableList(newEvents),
        auditInfo.update(now));
  }

  /**
   * 作成日時を取得します
   *
   * @return 作成日時
   */
  public Instant getCreatedAt() {
    return auditInfo.createdAt();
  }

  /**
   * 更新日時を取得します
   *
   * @return 更新日時
   */
  public Instant getUpdatedAt() {
    return auditInfo.updatedAt();
  }

  /**
   * エラーコードを取得します
   *
   * @return エラーコード
   */
  public @Nullable String getErrorCode() {
    return error.errorCode();
  }

  /**
   * エラーメッセージを取得します
   *
   * @return エラーメッセージ
   */
  public @Nullable String getErrorMessage() {
    return error.errorMessage();
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Payment payment)) {
      return false;
    }

    return Objects.equals(id, payment.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Payment{"
        + "id="
        + id
        + ", orderId="
        + orderId
        + ", amount="
        + amount
        + ", status="
        + status
        + ", paymentMethod='"
        + paymentMethod
        + '\''
        + ", externalTransactionId='"
        + externalTransactionId
        + '\''
        + ", error="
        + error
        + ", events="
        + events
        + ", auditInfo="
        + auditInfo
        + '}';
  }
}
