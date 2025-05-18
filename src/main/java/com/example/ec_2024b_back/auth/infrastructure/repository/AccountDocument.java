package com.example.ec_2024b_back.auth.infrastructure.repository;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.Authentication;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** MongoDBのaccountsコレクションに対応するドキュメントクラス. */
@Document(collection = "accounts")
public record AccountDocument(
    @Id String id,
    @Indexed(unique = true) String email,
    ImmutableList<AuthenticationInfo> authenticationInfos) {

  /** SpringData用のNo-argコンストラクタ */
  public AccountDocument() {
    this("", "", ImmutableList.of());
  }

  /** static factory method */
  public static AccountDocument create(
      String id, String email, ImmutableList<AuthenticationInfo> authenticationInfos) {
    return new AccountDocument(id, email, authenticationInfos);
  }

  /**
   * このドキュメントオブジェクトをAccountドメインモデルに変換します.
   *
   * @return 変換されたAccountオブジェクト
   */
  public Account toDomain() {
    return Account.reconstruct(
        new AccountId(UUID.fromString(id)),
        authenticationInfos.stream()
            .map(
                i -> {
                  if (i.type() == null) {
                    throw new IllegalStateException("Authentication type cannot be null");
                  }
                  return Authentication.of(i.type(), ImmutableMap.copyOf(i.credential()));
                })
            .collect(ImmutableList.toImmutableList()));
  }

  /** 認証情報 */
  public record AuthenticationInfo(@Nullable String type, ImmutableMap<String, String> credential) {

    /** SpringData用のNo-argコンストラクタ */
    public AuthenticationInfo() {
      this(null, ImmutableMap.of());
    }
  }
}
