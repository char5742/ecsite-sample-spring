package com.example.ec_2024b_back.user.domain.models;

import com.example.ec_2024b_back.account.domain.models.Account.AccountId;
import com.example.ec_2024b_back.share.domain.models.Address;

/** ユーザー情報 */
public record User(
    AccountId id,
    String firstName,
    /** 苗字 */
    String lastName,
    Address address,
    String telephone,
    String password) {

  /**
   * コンストラクタ
   *
   * @param id アカウントID
   * @param firstName 名
   * @param lastName 姓
   * @param address 住所
   * @param telephone 電話番号
   * @param password パスワードハッシュ
   */
  public User {
    if (firstName.isBlank()) {
      throw new IllegalArgumentException("名は空にできません");
    }
    if (lastName.isBlank()) {
      throw new IllegalArgumentException("姓は空にできません");
    }
    if (telephone.isBlank()) {
      throw new IllegalArgumentException("電話番号は空にできません");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("パスワードは空にできません");
    }
  }

  /**
   * フルネームを取得します
   *
   * @return 姓名（例: 山田 太郎）
   */
  public String getFullName() {
    return lastName + " " + firstName;
  }
}
