package com.example.ec_2024b_back.shopping.infrastructure.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/** 注文ドキュメントのリポジトリインターフェース */
@Repository
public interface OrderDocumentRepository extends ReactiveMongoRepository<OrderDocument, String> {

  /**
   * アカウントIDによる注文検索
   *
   * @param accountId アカウントID
   * @return 検索結果のFlux
   */
  Flux<OrderDocument> findByAccountId(String accountId);

  /**
   * アカウントIDと注文ステータスによる注文検索
   *
   * @param accountId アカウントID
   * @param status 注文ステータス
   * @return 検索結果のFlux
   */
  Flux<OrderDocument> findByAccountIdAndStatus(String accountId, String status);

  /**
   * 注文ステータスによる注文検索
   *
   * @param status 注文ステータス
   * @return 検索結果のFlux
   */
  Flux<OrderDocument> findByStatus(String status);
}
