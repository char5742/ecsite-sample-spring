package com.example.ec_2024b_back.user.infrastructure.repository.impl;

import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.user.infrastructure.repository.MongoUserRepository;
import com.example.ec_2024b_back.user.infrastructure.repository.document.UserDocument;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** MongoUserRepositoryの拡張実装クラス. Spring Dataでは提供されない機能を追加実装します。 */
@Component
@RequiredArgsConstructor
public class MongoUserRepositoryExtImpl {

  private final MongoUserRepository mongoUserRepository;

  /**
   * ユーザーをメールアドレス付きで保存するメソッド.
   *
   * @param user ドメインモデルのユーザー
   * @param email ユーザーのメールアドレス
   * @return 保存されたユーザーのドメインモデル
   */
  public Mono<User> saveWithEmail(User user, String email) {
    // 新規ユーザーの場合はIDを生成
    String userId = user.id() != null ? user.id().id() : UUID.randomUUID().toString();

    var document =
        new UserDocument(
            userId,
            user.firstName(),
            user.lastName(),
            email,
            user.password(),
            user.address(),
            user.telephone());

    // DocumentをMongoDBに保存して、結果をドメインモデルに変換して返す
    return mongoUserRepository.save(document).map(UserDocument::toDomain);
  }
}
