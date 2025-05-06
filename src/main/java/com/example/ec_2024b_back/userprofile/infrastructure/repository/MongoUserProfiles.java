package com.example.ec_2024b_back.userprofile.infrastructure.repository;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile.UserProfileId;
import com.example.ec_2024b_back.userprofile.domain.repositories.UserProfiles;
import com.google.errorprone.annotations.Var;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** UserProfilesインターフェースのMongoDB実装 */
@Component
@RequiredArgsConstructor
public class MongoUserProfiles implements UserProfiles {

  private final UserProfileDocumentRepository repository;

  @Override
  public Mono<UserProfile> findById(UserProfileId id) {
    return repository.findById(id.id().toString()).map(UserProfileDocument::toDomain);
  }

  @Override
  public Mono<UserProfile> findByAccountId(AccountId accountId) {
    return repository.findByAccountId(accountId.id().toString()).map(UserProfileDocument::toDomain);
  }

  @Override
  public Mono<UserProfile> save(UserProfile userProfile) {
    // ドキュメント変換時にアカウントIDを取得できないため、関連付け情報は維持する
    return Mono.just(userProfile)
        .flatMap(
            profile -> {
              // まず既存のドキュメントを確認して、accountIdがあれば保持する
              return repository
                  .findById(profile.getId().id().toString())
                  .defaultIfEmpty(new UserProfileDocument())
                  .map(
                      existingDoc -> {
                        @Var
                        UserProfileDocument doc = UserProfileDocument.fromDomain(profile, null);
                        // 既存のドキュメントからアカウントIDを保持
                        if (existingDoc.id() != null && existingDoc.accountId() != null) {
                          // recordはイミュータブルなので、新しいインスタンスを作成
                          doc =
                              new UserProfileDocument(
                                  doc.id(), doc.name(), existingDoc.accountId(), doc.addresses());
                        }
                        return doc;
                      });
            })
        .flatMap(repository::save)
        .map(UserProfileDocument::toDomain);
  }
}
