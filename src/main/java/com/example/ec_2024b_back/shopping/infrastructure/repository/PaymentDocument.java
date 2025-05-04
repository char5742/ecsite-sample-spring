package com.example.ec_2024b_back.shopping.infrastructure.repository;

import com.example.ec_2024b_back.shopping.OrderId;
import com.example.ec_2024b_back.shopping.PaymentId;
import com.example.ec_2024b_back.shopping.domain.models.Payment;
import com.example.ec_2024b_back.shopping.domain.models.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** MongoDBに保存される支払いドキュメント */
@Document(collection = "payments")
@Data
@AllArgsConstructor
public class PaymentDocument {
  @Id private String id;

  @Indexed private String orderId;
  private BigDecimal amount;

  @Indexed private String status;
  private String paymentMethod;
  private @Nullable String externalTransactionId;

  // エラー情報
  private @Nullable String errorCode;
  private @Nullable String errorMessage;

  // 監査情報
  private Instant createdAt;
  private Instant updatedAt;

  /** SpringData用のNo-argコンストラクタ 全てのフィールドにnon-nullが保証される値を設定 */
  public PaymentDocument() {
    this.id = "";
    this.orderId = "";
    this.amount = BigDecimal.ZERO;
    this.status = PaymentStatus.PENDING.name();
    this.paymentMethod = "";
    this.externalTransactionId = null;
    this.errorCode = null;
    this.errorMessage = null;
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
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
