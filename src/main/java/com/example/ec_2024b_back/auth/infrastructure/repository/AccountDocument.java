package com.example.ec_2024b_back.auth.infrastructure.repository;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.Authentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    List<AuthenticationInfo> authenticationInfos) {

  /** SpringData用のNo-argコンストラクタ */
  public AccountDocument() {
    this("", "", List.of());
  }

  /** static factory method */
  public static AccountDocument create(
      String id, String email, List<AuthenticationInfo> authenticationInfos) {
    return new AccountDocument(id, email, new ArrayList<>(authenticationInfos));
  }

  /**
   * このドキュメントオブジェクトをAccountドメインモデルに変換します.
   *
   * @return 変換されたAccountオブジェクト
   */
  public Account toDomain() {
    List<Authentication> authList =
        authenticationInfos.stream()
            .map(
                i -> {
                  if (i.type() == null) {
                    throw new IllegalStateException("Authentication type cannot be null");
                  }
                  return Authentication.of(i.type(), new HashMap<>(i.credential()));
                })
            .toList();
    return Account.reconstruct(new AccountId(UUID.fromString(id)), authList);
  }

  /** 認証情報 */
  public record AuthenticationInfo(@Nullable String type, Map<String, String> credential) {

    /** SpringData用のNo-argコンストラクタ */
    public AuthenticationInfo() {
      this(null, Map.of());
    }
  }
}
