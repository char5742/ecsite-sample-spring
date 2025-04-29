package com.example.ec_2024b_back.auth.infrastructure.repository;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.Authentication;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.jspecify.annotations.NullUnmarked;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** MongoDBのaccountsコレクションに対応するドキュメントクラス. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@NullUnmarked
@Document(collection = "accounts")
public class AccountDocument {

  /** アカウントID */
  @Id private String id;

  /** 認証情報のリスト */
  private List<AuthenticationInfo> authenticationInfos;

  /**
   * このドキュメントオブジェクトをAccountドメインモデルに変換します.
   *
   * @return 変換されたAccountオブジェクト
   */
  public Account toDomain() {
    return Account.reconstruct(
        new Account.AccountId(UUID.fromString(this.id)),
        this.authenticationInfos.stream()
            .map(i -> Authentication.of(i.type, i.credential))
            .collect(ImmutableList.toImmutableList()));
  }

  record AuthenticationInfo(String type, ImmutableMap<String, String> credential) {}
}
