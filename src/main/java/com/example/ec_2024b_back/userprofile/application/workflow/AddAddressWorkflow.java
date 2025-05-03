package com.example.ec_2024b_back.userprofile.application.workflow;

import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.userprofile.domain.models.Address;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile.UserProfileId;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Mono;

/** 住所追加を実行するワークフロー */
public interface AddAddressWorkflow {

  /** ユーザープロファイルを取得する */
  @FunctionalInterface
  public interface FindUserProfileByIdStep extends Function<Context.Input, Mono<Context.Found>> {}

  /** 住所を作成する */
  @FunctionalInterface
  public interface CreateAddressStep extends Function<Context.Found, Context.AddressCreated> {}

  /** ユーザープロファイルに住所を追加する */
  @FunctionalInterface
  public interface AddAddressToUserProfileStep
      extends Function<Context.AddressCreated, Mono<Context.AddressAdded>> {}

  sealed interface Context
      permits Context.Input, Context.Found, Context.AddressCreated, Context.AddressAdded {
    record Input(
        UserProfileId userProfileId,
        String name,
        String postalCode,
        String prefecture,
        String city,
        String street,
        @Nullable String building,
        String phoneNumber,
        boolean isDefault)
        implements Context {}

    record Found(
        UserProfile userProfile,
        String name,
        String postalCode,
        String prefecture,
        String city,
        String street,
        @Nullable String building,
        String phoneNumber,
        boolean isDefault)
        implements Context {}

    record AddressCreated(UserProfile userProfile, Address address) implements Context {}

    record AddressAdded(UserProfile userProfile) implements Context {}
  }

  /**
   * 住所追加処理を実行します
   *
   * @param userProfileId ユーザープロファイルID
   * @param name 氏名
   * @param postalCode 郵便番号
   * @param prefecture 都道府県
   * @param city 市区町村
   * @param street 番地
   * @param building 建物名・部屋番号（任意）
   * @param phoneNumber 電話番号
   * @param isDefault デフォルト住所フラグ
   * @return 住所が追加されたユーザープロファイルを含むMono
   */
  Mono<UserProfile> execute(
      UserProfileId userProfileId,
      String name,
      String postalCode,
      String prefecture,
      String city,
      String street,
      @Nullable String building,
      String phoneNumber,
      boolean isDefault);

  /** ユーザープロファイルが見つからない場合のカスタム例外 */
  public static class UserProfileNotFoundException extends DomainException {
    public UserProfileNotFoundException(String userProfileId) {
      super("ユーザープロファイルID: " + userProfileId + " のプロファイルが見つかりません");
    }
  }
}
