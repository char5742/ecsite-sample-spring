package com.example.ec_2024b_back.userprofile.infrastructure.repository;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.userprofile.domain.models.Address;
import com.example.ec_2024b_back.userprofile.domain.models.AddressId;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile.UserProfileId;
import com.google.common.collect.ImmutableList;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** ユーザープロファイルのMongoDBドキュメントクラス */
@Document(collection = "user_profiles")
public record UserProfileDocument(
    @Id @Nullable String id,
    @Nullable String name,
    @Indexed(unique = true) @Nullable String accountId,
    @Nullable ImmutableList<AddressDocument> addresses) {

  /** SpringData用のNo-argコンストラクタ */
  public UserProfileDocument() {
    this(null, null, null, null);
  }

  /*
   * ドメインモデルからドキュメントを生成
   */
  public static UserProfileDocument fromDomain(
      UserProfile userProfile, @Nullable AccountId accountId) {
    // アドレスがない場合はnullを返す
    if (userProfile.getAddresses().isEmpty()) {
      return new UserProfileDocument(
          userProfile.getId().id().toString(),
          userProfile.getName(),
          accountId != null ? accountId.id().toString() : null,
          null);
    }

    // アドレスがある場合はImmutableListに変換
    ImmutableList<AddressDocument> addressDocs =
        userProfile.getAddresses().stream()
            .map(AddressDocument::fromDomain)
            .collect(ImmutableList.toImmutableList());

    return new UserProfileDocument(
        userProfile.getId().id().toString(),
        userProfile.getName(),
        accountId != null ? accountId.id().toString() : null,
        addressDocs);
  }

  /*
   * ドキュメントからドメインモデルを再構築
   */
  public UserProfile toDomain() {
    ImmutableList<Address> domainAddresses =
        addresses != null
            ? addresses.stream()
                .map(AddressDocument::toDomain)
                .collect(ImmutableList.toImmutableList())
            : ImmutableList.of();

    // nameがnullまたは空の場合、デフォルトの名前を使用
    String validName = (name == null || name.isBlank()) ? "未設定" : name;

    return UserProfile.reconstruct(
        new UserProfileId(UUID.fromString(id)), validName, domainAddresses);
  }

  /*
   * 住所のドキュメントクラス
   */
  public record AddressDocument(
      String id,
      String name,
      String postalCode,
      String prefecture,
      String city,
      String street,
      String building,
      String phoneNumber,
      boolean isDefault) {

    /** SpringData用のNo-argコンストラクタ */
    public AddressDocument() {
      this("", "", "", "", "", "", "", "", /* isDefault= */ false);
    }

    public static AddressDocument fromDomain(Address address) {
      return new AddressDocument(
          address.id().id().toString(),
          address.name(),
          address.postalCode(),
          address.prefecture(),
          address.city(),
          address.street(),
          address.building() != null ? address.building() : "", // buildingがnullの場合は空文字を使用
          address.phoneNumber(),
          address.isDefault());
    }

    public Address toDomain() {
      return new Address(
          AddressId.of(id),
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
}
