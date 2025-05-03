package com.example.ec_2024b_back.userprofile.application.workflow;

import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile.UserProfileId;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** 住所削除を実行するワークフロー */
public interface RemoveAddressWorkflow {

  /** ユーザープロファイルを取得する */
  @FunctionalInterface
  public interface FindUserProfileByIdStep extends Function<Context.Input, Mono<Context.Found>> {}

  /** ユーザープロファイルから住所を削除する */
  @FunctionalInterface
  public interface RemoveAddressFromUserProfileStep
      extends Function<Context.Found, Mono<Context.AddressRemoved>> {}

  sealed interface Context permits Context.Input, Context.Found, Context.AddressRemoved {
    record Input(UserProfileId userProfileId, String addressId) implements Context {}

    record Found(UserProfile userProfile, String addressId) implements Context {}

    record AddressRemoved(UserProfile userProfile) implements Context {}
  }

  /**
   * 住所削除処理を実行します
   *
   * @param userProfileId ユーザープロファイルID
   * @param addressId 住所ID
   * @return 住所が削除されたユーザープロファイルを含むMono
   */
  Mono<UserProfile> execute(UserProfileId userProfileId, String addressId);

  /** ユーザープロファイルが見つからない場合のカスタム例外 */
  public static class UserProfileNotFoundException extends DomainException {
    public UserProfileNotFoundException(String userProfileId) {
      super("ユーザープロファイルID: " + userProfileId + " のプロファイルが見つかりません");
    }
  }

  /** 住所が見つからない場合のカスタム例外 */
  public static class AddressNotFoundException extends DomainException {
    public AddressNotFoundException(String addressId) {
      super("住所ID: " + addressId + " の住所が見つかりません");
    }
  }
}
