package com.example.ec_2024b_back.auth.infrastructure.repository;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.Authentication;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** MongoDBのaccountsコレクションに対応するドキュメントクラス. */
@Document(collection = "accounts")
public record AccountDocument(
    /** アカウントID */
    @Id String id,

    /** メールアドレス（検索用にインデックス追加） */
    @Indexed(unique = true) String email,

    /** 認証情報のリスト */
    ImmutableList<AuthenticationInfo> authenticationInfos) {

  /** SpringData用のNo-argコンストラクタ */
  public AccountDocument() {
    this("", "", ImmutableList.of());
  }

  /**
   * このドキュメントオブジェクトをAccountドメインモデルに変換します.
   *
   * @return 変換されたAccountオブジェクト
   */
  public Account toDomain() {
    return Account.reconstruct(
        new AccountId(UUID.fromString(this.id)),
        this.authenticationInfos.stream()
            .map(i -> Authentication.of(i.type, i.credential))
            .collect(ImmutableList.toImmutableList()));
  }

  public record AuthenticationInfo(String type, ImmutableMap<String, String> credential) {
    /** SpringData用のNo-argコンストラクタ */
    public AuthenticationInfo() {
      this("", ImmutableMap.of());
    }
  }
}
