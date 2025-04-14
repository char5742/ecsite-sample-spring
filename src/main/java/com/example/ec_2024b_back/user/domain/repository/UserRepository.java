package com.example.ec_2024b_back.user.domain.repository;

import com.example.ec_2024b_back.user.domain.models.User;
import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Mono; // Reactive対応のためMonoを使用

/** Userエンティティのリポジトリインターフェース. */
public interface UserRepository {

  /**
   * メールアドレスでユーザーを検索します.
   *
   * @param email 検索するメールアドレス
   * @return 検索結果 (成功時はOption<User>、失敗時はFailure) を含むTryをMonoでラップしたもの
   */
  Mono<Try<Option<User>>> findByEmail(String email);

  // 必要に応じて他のメソッド（save, findByIdなど）を追加
}
