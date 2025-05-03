package com.example.ec_2024b_back.userprofile.application.workflow;

import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile.UserProfileId;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** ユーザープロファイル更新を実行するワークフロー */
public interface UpdateUserProfileWorkflow {

  /** ユーザープロファイルを取得する */
  @FunctionalInterface
  public interface FindUserProfileByIdStep extends Function<Context.Input, Mono<Context.Found>> {}

  /** ユーザープロファイルを更新する */
  @FunctionalInterface
  public interface UpdateUserProfileStep extends Function<Context.Found, Mono<Context.Updated>> {}

  sealed interface Context permits Context.Input, Context.Found, Context.Updated {
    record Input(UserProfileId userProfileId, String name) implements Context {}

    record Found(UserProfile userProfile, String name) implements Context {}

    record Updated(UserProfile userProfile) implements Context {}
  }

  /**
   * ユーザープロファイル更新処理を実行します
   *
   * @param userProfileId ユーザープロファイルID
   * @param name 新しいユーザー名
   * @return 更新されたユーザープロファイルを含むMono
   */
  Mono<UserProfile> execute(UserProfileId userProfileId, String name);

  /** ユーザープロファイルが見つからない場合のカスタム例外 */
  public static class UserProfileNotFoundException extends DomainException {
    public UserProfileNotFoundException(String userProfileId) {
      super("ユーザープロファイルID: " + userProfileId + " のプロファイルが見つかりません");
    }
  }
}
