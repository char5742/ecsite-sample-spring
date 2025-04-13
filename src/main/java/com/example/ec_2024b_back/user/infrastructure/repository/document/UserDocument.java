package com.example.ec_2024b_back.user.infrastructure.repository.document;

import com.example.ec_2024b_back.share.domain.models.Address;
import lombok.AllArgsConstructor; // Address値オブジェクトを使用
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** MongoDBのusersコレクションに対応するドキュメントクラス. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserDocument {

  @Id private String id; // AccountIdに対応

  private String firstName;
  private String lastName;

  @Indexed(unique = true) // emailはユニーク制約とインデックスを設定
  private String email;

  private String password; // ハッシュ化されたパスワード

  private Address address; // 住所情報を埋め込み

  private String telephone;
}
