package com.example.ec_2024b_back.shopping.domain.repositories;

import com.example.ec_2024b_back.shopping.domain.models.OrderId;
import com.example.ec_2024b_back.shopping.domain.models.Payment;
import com.example.ec_2024b_back.shopping.domain.models.PaymentId;
import com.example.ec_2024b_back.shopping.domain.models.PaymentStatus;
import org.jmolecules.ddd.types.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** 支払いリポジトリのインターフェース */
public interface Payments extends Repository<Payment, PaymentId> {

  /**
   * 支払いIDにより支払いを検索します
   *
   * @param id 支払いID
   * @return 検索結果
   */
  Mono<Payment> findById(PaymentId id);

  /**
   * 注文IDにより支払いを検索します
   *
   * @param orderId 注文ID
   * @return 検索結果
   */
  Mono<Payment> findByOrderId(OrderId orderId);

  /**
   * 支払いステータスに紐づく支払いをすべて検索します
   *
   * @param status 支払いステータス
   * @return 支払いのストリーム
   */
  Flux<Payment> findByStatus(PaymentStatus status);

  /**
   * 支払いを保存します
   *
   * @param payment 保存する支払い
   * @return 保存された支払い
   */
  Mono<Payment> save(Payment payment);
}
