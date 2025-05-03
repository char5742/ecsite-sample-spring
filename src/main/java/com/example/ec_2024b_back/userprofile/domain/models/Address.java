package com.example.ec_2024b_back.userprofile.domain.models;

import org.jspecify.annotations.Nullable;

public record Address(
    AddressId id,
    String name,
    String postalCode,
    String prefecture,
    String city,
    String street,
    @Nullable String building,
    String phoneNumber,
    boolean isDefault) {

  public Address {
    if (name.isBlank()) {
      throw new IllegalArgumentException("氏名は空であってはなりません");
    }
    if (postalCode.isBlank()) {
      throw new IllegalArgumentException("郵便番号は空であってはなりません");
    }
    if (prefecture.isBlank()) {
      throw new IllegalArgumentException("都道府県は空であってはなりません");
    }
    if (city.isBlank()) {
      throw new IllegalArgumentException("市区町村は空であってはなりません");
    }
    if (street.isBlank()) {
      throw new IllegalArgumentException("番地は空であってはなりません");
    }
    if (phoneNumber.isBlank()) {
      throw new IllegalArgumentException("電話番号は空であってはなりません");
    }
  }

  public Address withDefault(boolean isDefault) {
    return new Address(
        id, name, postalCode, prefecture, city, street, building, phoneNumber, isDefault);
  }
}
