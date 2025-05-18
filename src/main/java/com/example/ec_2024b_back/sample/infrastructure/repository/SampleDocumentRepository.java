package com.example.ec_2024b_back.sample.infrastructure.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDBのリアクティブリポジトリ。
 *
 * <p>このインターフェースは、Spring Data MongoDBの使用例を示します。 基本的なCRUD操作は自動的に実装されます。
 */
public interface SampleDocumentRepository extends ReactiveMongoRepository<SampleDocument, String> {
  /**
   * 名前でサンプルを検索します。
   *
   * @param name 名前
   * @return 見つかったサンプルのFlux
   */
  Flux<SampleDocument> findByName(String name);
}
