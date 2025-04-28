package com.example.ec_2024b_back.user.infrastructure.repository.impl;

import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.user.domain.repository.UserRepository;
import com.example.ec_2024b_back.user.infrastructure.repository.MongoUserRepository;
import com.example.ec_2024b_back.user.infrastructure.repository.document.UserDocument;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** UserRepositoryの実装クラス. MongoUserRepositoryとその拡張実装を組み合わせてUserRepositoryインターフェースを実装します。 */
@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final MongoUserRepository mongoUserRepository;
  private final MongoUserRepositoryExtImpl mongoUserRepositoryExt;

  @Override
  public Mono<Optional<User>> findByEmail(String email) {
    return mongoUserRepository.findByEmail(email);
  }

  @Override
  public Mono<User> save(User user) {
    if (user.id() == null) {
      throw new IllegalArgumentException("ユーザーIDがnullです。新規ユーザーの場合はsaveWithEmailを使用してください。");
    }

    // Userドメインモデルを対応するUserDocumentに変換
    var document =
        new UserDocument(
            user.id().id(),
            user.firstName(),
            user.lastName(),
            "", // メールアドレスはここでは不明なため空で設定
            user.password(),
            user.address(),
            user.telephone());

    // DocumentをMongoDBに保存して、結果をドメインモデルに変換して返す
    return mongoUserRepository.save(document).map(UserDocument::toDomain);
  }

  @Override
  public Mono<User> saveWithEmail(User user, String email) {
    return mongoUserRepositoryExt.saveWithEmail(user, email);
  }
}
