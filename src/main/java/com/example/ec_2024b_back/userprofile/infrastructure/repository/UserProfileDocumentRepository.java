package com.example.ec_2024b_back.userprofile.infrastructure.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/** ユーザープロファイルドキュメントのリポジトリインターフェース */
@Repository
public interface UserProfileDocumentRepository
    extends ReactiveMongoRepository<UserProfileDocument, String> {

  /** アカウントIDでユーザープロファイルを検索 */
  Mono<UserProfileDocument> findByAccountId(String accountId);
}
