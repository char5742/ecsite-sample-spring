package com.example.ec_2024b_back.user.domain.repository;

import com.example.ec_2024b_back.user.domain.models.User; // Reactive対応のためMonoを使用
import java.util.Optional;
import reactor.core.publisher.Mono;

/** Userエンティティのリポジトリインターフェース. */
public interface UserRepository {

  /**
   * メールアドレスでユーザーを検索します.
   *
   * @param email 検索するメールアドレス
   * @return 検索結果を含むOptionalをMonoでラップしたもの
   */
  Mono<Optional<User>> findByEmail(String email);

  // 必要に応じて他のメソッド（save, findByIdなど）を追加
}
