package com.example.ec_2024b_back.shopping.infrastructure.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/** カートドキュメントのリポジトリインターフェース */
@Repository
public interface CartDocumentRepository extends ReactiveMongoRepository<CartDocument, String> {

  /**
   * アカウントIDによりカートを検索します
   *
   * @param accountId アカウントID
   * @return 検索結果
   */
  Mono<CartDocument> findByAccountId(String accountId);
}
