package com.example.ec_2024b_back.user.infrastructure.repository;

import com.example.ec_2024b_back.account.domain.models.Account;
import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.user.infrastructure.repository.document.UserDocument;
import io.vavr.control.Option;
import io.vavr.control.Try;
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
   * findDocumentByEmailの結果をドメインモデル(User)に変換し、TryとOptionでラップする.
   *
   * @param email 検索するメールアドレス
   * @return 検索結果 (成功時はOption<User>、失敗時はFailure) を含むTryをMonoでラップしたもの
   */
  @Override
  default Mono<Try<Option<User>>> findByEmail(String email) {
    return findDocumentByEmail(email)
        .map(userDocument -> Option.of(convertToDomain(userDocument)))
        .defaultIfEmpty(Option.none())
        .map(Try::success)
        .onErrorResume(e -> Mono.just(Try.failure(e)));
  }

  /**
   * UserDocumentをUserドメインモデルに変換するヘルパーメソッド. (実際の変換ロジックはここに実装)
   *
   * @param document 変換元のUserDocument
   * @return 変換後のUserドメインモデル
   */
  private User convertToDomain(UserDocument document) {
    if (document == null) {
      // This case should ideally be handled before calling this method,
      // but returning null or throwing an exception are options.
      // Returning Option<User> might be better.
      return null;
    }
    return new User(
        new Account.AccountId(document.getId()),
        document.getFirstName(),
        document.getLastName(),
        document.getAddress(),
        document.getTelephone());
  }
}
