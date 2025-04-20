package com.example.ec_2024b_back.user.infrastructure.repository.document;

import com.example.ec_2024b_back.account.domain.models.Account;
import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.user.domain.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullUnmarked;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** MongoDBのusersコレクションに対応するドキュメントクラス. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@NullUnmarked
@Document(collection = "users")
public class UserDocument {

  @Id private String id;

  private String firstName;
  private String lastName;

  @Indexed(unique = true)
  private String email;

  private String password;
  private Address address;

  private String telephone;

  /**
   * このドキュメントオブジェクトをUserドメインモデルに変換します.
   *
   * @return 変換されたUserオブジェクト
   * @throws IllegalArgumentException idがnullの場合
   */
  public User toDomain() {
    if (this.id == null) {
      throw new IllegalArgumentException("UserDocumentのIDがnullです。ドメインオブジェクトに変換できません。");
    }
    return new User(
        new Account.AccountId(this.id),
        this.firstName,
        this.lastName,
        this.address,
        this.telephone,
        this.password);
  }
}
