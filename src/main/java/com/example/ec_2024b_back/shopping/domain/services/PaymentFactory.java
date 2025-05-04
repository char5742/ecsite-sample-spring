package com.example.ec_2024b_back.shopping.domain.services;

import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.shopping.domain.models.Order;
import com.example.ec_2024b_back.shopping.domain.models.Payment;
import com.example.ec_2024b_back.shopping.domain.models.PaymentId;
import com.google.errorprone.annotations.Var;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

/** 支払いオブジェクトを生成するファクトリークラス */
@RequiredArgsConstructor
public class PaymentFactory {
  private final IdGenerator idGenerator;
  private final Clock clock;

  /**
   * 注文に対する新しい支払いを開始します
   *
   * @param order 注文
   * @param paymentMethod 支払い方法
   * @return 開始された支払い
   */
  public Payment initiatePayment(Order order, String paymentMethod) {
    var paymentId = new PaymentId(idGenerator.newId());
    var now = Instant.now(clock);

    return Payment.initiate(paymentId, order.getId(), order.getTotalAmount(), paymentMethod, now);
  }

  /**
   * 外部システムから返された支払い情報で支払いを開始します
   *
   * @param order 注文
   * @param paymentMethod 支払い方法
   * @param externalTransactionId 外部トランザクションID
   * @return 開始された支払い
   */
  public Payment initiateExternalPayment(
      Order order, String paymentMethod, @Nullable String externalTransactionId) {
    var paymentId = new PaymentId(idGenerator.newId());
    var now = Instant.now(clock);

    @Var
    Payment payment =
        Payment.initiate(paymentId, order.getId(), order.getTotalAmount(), paymentMethod, now);

    // 外部トランザクションIDがある場合は承認済み状態に更新
    if (externalTransactionId != null && !externalTransactionId.isBlank()) {
      payment = payment.authorize(externalTransactionId, now, now);
    }

    return payment;
  }
}
