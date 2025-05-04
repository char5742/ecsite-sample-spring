package com.example.ec_2024b_back.shopping.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** 支払いドキュメントのリポジトリインターフェース */
public interface PaymentDocumentRepository extends ReactiveCrudRepository<PaymentDocument, String> {
  /**
   * 注文IDにより支払いドキュメントを検索します
   *
   * @param orderId 注文ID
   * @return 検索結果
   */
  Mono<PaymentDocument> findByOrderId(String orderId);

  /**
   * 支払いステータスに紐づく支払いドキュメントをすべて検索します
   *
   * @param status 支払いステータス
   * @return 支払いドキュメントのストリーム
   */
  Flux<PaymentDocument> findByStatus(String status);
}
