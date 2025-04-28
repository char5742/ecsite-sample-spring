package com.example.ec_2024b_back.model;

import com.example.ec_2024b_back.share.domain.models.Address;
import java.util.Objects;
import lombok.Data;
import org.jspecify.annotations.Nullable;

/** ユーザー登録のリクエストDTO. */
@Data
public class RegisterUserDto {
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private Address.Zipcode zipcode;
  private Address.Prefecture prefecture;
  private Address.Municipalities municipalities;
  private Address.DetailAddress detailAddress;
  private String telephone;

  /**
   * DTOから住所オブジェクトを生成します.
   *
   * @return 住所オブジェクト
   */
  public Address toAddress() {
    return new Address(this.zipcode, this.prefecture, this.municipalities, this.detailAddress);
  }

  /**
   * 基本的な入力検証を行います.
   *
   * @return 検証エラーメッセージ（検証に成功した場合はnull）
   */
  public @Nullable String validate() {
    if (Objects.isNull(firstName) || firstName.isBlank()) {
      return "名は必須です";
    }
    if (Objects.isNull(lastName) || lastName.isBlank()) {
      return "姓は必須です";
    }
    if (Objects.isNull(email) || email.isBlank()) {
      return "メールアドレスは必須です";
    }
    if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
      return "メールアドレスの形式が不正です";
    }
    if (Objects.isNull(password) || password.isBlank()) {
      return "パスワードは必須です";
    }
    if (password.length() < 8) {
      return "パスワードは8文字以上である必要があります";
    }
    if (Objects.isNull(zipcode)) {
      return "郵便番号は必須です";
    }
    if (Objects.isNull(prefecture)) {
      return "都道府県は必須です";
    }
    if (Objects.isNull(municipalities)) {
      return "市区町村は必須です";
    }
    if (Objects.isNull(detailAddress)) {
      return "番地・建物名等は必須です";
    }
    if (Objects.isNull(telephone) || telephone.isBlank()) {
      return "電話番号は必須です";
    }
    if (!telephone.matches("^\\d{2,4}-?\\d{2,4}-?\\d{3,4}$")) {
      return "電話番号の形式が不正です（例: 03-1234-5678）";
    }
    return null;
  }
}
