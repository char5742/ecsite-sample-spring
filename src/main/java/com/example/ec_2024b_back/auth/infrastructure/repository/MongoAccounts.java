package com.example.ec_2024b_back.auth.infrastructure.repository;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.Authentication;
import com.example.ec_2024b_back.auth.domain.models.EmailAuthentication;
import com.example.ec_2024b_back.auth.domain.repositories.Accounts;
import com.example.ec_2024b_back.share.domain.models.Email;
import com.google.errorprone.annotations.Var;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  /**
   * Accountドメインモデルを保存します。
   *
   * @param account 保存するアカウント
   * @return 保存したアカウントを含むMono
   */
  @Override
  default Mono<Account> save(Account account) {
    // アカウントからドキュメントに変換
    AccountDocument doc = toDocument(account);

    // ドキュメントを保存し、結果をドメインモデルに戻す
    return save(doc).map(AccountDocument::toDomain);
  }

  /** ドメインモデルからドキュメントに変換する内部メソッド */
  default AccountDocument toDocument(Account account) {
    List<AccountDocument.AuthenticationInfo> authInfoList = new ArrayList<>();

    // メールアドレスを特定するための変数
    @Var var emailValue = ""; // 空文字列で初期化して、nullにならないようにする

    // 認証情報の変換
    for (Authentication auth : account.getAuthentications()) {
      @Var Map<String, String> credential = null;

      if (auth instanceof EmailAuthentication emailAuth) {
        // EmailAuthenticationの場合、メールアドレスとパスワードをマップに格納
        HashMap<String, String> map = new HashMap<>();
        map.put("email", emailAuth.email().value());
        map.put("password", emailAuth.password().value());
        credential = map;

        // メールアドレスを記録
        emailValue = emailAuth.email().value();
      }

      if (credential != null) {
        authInfoList.add(new AccountDocument.AuthenticationInfo(auth.type(), credential));
      }
    }

    // ドキュメントを作成して返す
    return new AccountDocument(
        account.getId().id().toString(),
        emailValue, // 見つかったメールアドレスを設定
        authInfoList);
  }
}
