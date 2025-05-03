package com.example.ec_2024b_back.userprofile.domain.services;

import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.userprofile.domain.models.Address;
import com.example.ec_2024b_back.userprofile.domain.models.AddressId;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

/** 住所を作成するファクトリー */
@Component
@RequiredArgsConstructor
public class AddressFactory {
  private final IdGenerator idGen;

  /**
   * 新しい住所を作成します
   *
   * @param name 氏名
   * @param postalCode 郵便番号
   * @param prefecture 都道府県
   * @param city 市区町村
   * @param street 番地
   * @param building 建物名（任意）
   * @param phoneNumber 電話番号
   * @param isDefault デフォルト住所かどうか
   * @return 作成された住所
   */
  public Address create(
      String name,
      String postalCode,
      String prefecture,
      String city,
      String street,
      @Nullable String building,
      String phoneNumber,
      boolean isDefault) {
    return new Address(
        new AddressId(idGen.newId()),
        name,
        postalCode,
        prefecture,
        city,
        street,
        building,
        phoneNumber,
        isDefault);
  }
}
