package com.example.ec_2024b_back.user.infrastructure.repository;

import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.user.domain.repository.UserRepository;
import com.example.ec_2024b_back.user.infrastructure.repository.document.UserDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/** Spring Data MongoDB Reactiveを使用したUserRepositoryの実装. */
public interface MongoUserRepository
    extends ReactiveMongoRepository<UserDocument, String>, UserRepository {

  /**
   * EmailでUserDocumentを検索するメソッド (Spring Dataが自動実装). ReactiveMongoRepositoryが提供するfindByXXXメソッドを利用.
   *
   * @param email 検索するメールアドレス
   * @return 見つかった場合はUserDocumentを含むMono、見つからない場合は空のMono
   */
  Mono<UserDocument> findDocumentByEmail(String email);

  /**
   * UserRepositoryインターフェースのfindByEmailメソッドをデフォルト実装で提供.
   * findDocumentByEmailの結果をドメインモデル(User)に変換し、Optionalでラップする.
   *
   * @param email 検索するメールアドレス
   * @return 検索結果を含むOptionalをMonoでラップしたもの
   */
  @Override
  default Mono<Optional<User>> findByEmail(String email) {
    return findDocumentByEmail(email)
        .map(userDocument -> Optional.ofNullable(userDocument.toDomain()))
        .defaultIfEmpty(Optional.empty());
  }
}
