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

  /**
   * ユーザーを保存します.
   *
   * @param user 保存するユーザー
   * @return 保存されたユーザー
   */
  Mono<User> save(User user);

  /**
   * メールアドレスを含めてユーザーを保存します. 新規ユーザー登録時に使用します。
   *
   * @param user 保存するユーザー
   * @param email ユーザーのメールアドレス
   * @return 保存されたユーザー
   */
  Mono<User> saveWithEmail(User user, String email);
}
