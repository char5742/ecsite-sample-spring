package com.example.ec_2024b_back.shopping.infrastructure.repository;

import com.example.ec_2024b_back.shopping.OrderId;
import com.example.ec_2024b_back.shopping.PaymentId;
import com.example.ec_2024b_back.shopping.domain.models.Payment;
import com.example.ec_2024b_back.shopping.domain.models.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** MongoDBに保存される支払いドキュメント */
@Document(collection = "payments")
public record PaymentDocument(
    @Id String id,
    @Indexed String orderId,
    BigDecimal amount,
    @Indexed String status,
    String paymentMethod,
    @Nullable String externalTransactionId,
    // エラー情報
    @Nullable String errorCode,
    @Nullable String errorMessage,
    // 監査情報
    Instant createdAt,
    Instant updatedAt) {

  /** SpringData用のNo-argコンストラクタ */
  public PaymentDocument() {
    this(
        "",
        "",
        BigDecimal.ZERO,
        PaymentStatus.PENDING.name(),
        "",
        null,
        null,
        null,
        Instant.now(),
        Instant.now());
  }

  /**
   * ドメインモデルからドキュメントを作成します
   *
   * @param payment 支払いドメインモデル
   * @return 作成されたドキュメント
   */
  public static PaymentDocument fromDomain(Payment payment) {
    return new PaymentDocument(
        payment.getId().id().toString(),
        payment.getOrderId().id().toString(),
        payment.getAmount(),
        payment.getStatus().name(),
        payment.getPaymentMethod(),
        payment.getExternalTransactionId(),
        payment.getErrorCode(),
        payment.getErrorMessage(),
        payment.getCreatedAt(),
        payment.getUpdatedAt());
  }

  /**
   * ドキュメントからドメインモデルを作成します
   *
   * @return 作成されたドメインモデル
   */
  public Payment toDomain() {
    return Payment.reconstruct(
        PaymentId.of(id),
        OrderId.of(orderId),
        amount,
        PaymentStatus.valueOf(status),
        paymentMethod,
        externalTransactionId,
        errorCode,
        errorMessage,
        createdAt,
        updatedAt);
  }
}
