package com.example.ec_2024b_back.auth.infrastructure.repository;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.repositories.Accounts;
import com.example.ec_2024b_back.share.domain.models.Email;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/** Spring Data MongoDB Reactiveを使用したUserRepositoryの実装. */
@Repository
public interface MongoAccounts extends ReactiveMongoRepository<AccountDocument, String>, Accounts {

  /**
   * EmailでUserDocumentを検索するメソッド (Spring Dataが自動実装). ReactiveMongoRepositoryが提供するfindByXXXメソッドを利用.
   *
   * @param email 検索するメールアドレス
   * @return 見つかった場合はUserDocumentを含むMono、見つからない場合は空のMono
   */
  Mono<AccountDocument> findDocumentByEmail(String email);

  /**
   * UserRepositoryインターフェースのfindByEmailメソッドをデフォルト実装で提供.
   * findDocumentByEmailの結果をドメインモデル(User)に変換し、Optionalでラップする.
   *
   * @param email 検索するメールアドレス
   * @return 検索結果を含むOptionalをMonoでラップしたもの
   */
  @Override
  default Mono<Account> findByEmail(Email email) {
    return findDocumentByEmail(email.value()).map(accountDocument -> accountDocument.toDomain());
  }
}
