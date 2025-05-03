package com.example.ec_2024b_back.userprofile.application.workflow;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** ユーザープロファイル作成を実行するワークフロー */
public interface CreateUserProfileWorkflow {

  /** ユーザープロファイルを作成する */
  @FunctionalInterface
  public interface CreateUserProfileStep extends Function<Context.Input, Mono<Context.Created>> {}

  /** プロファイルとアカウントを関連付ける */
  @FunctionalInterface
  public interface AssociateWithAccountStep
      extends Function<Context.Created, Mono<Context.Associated>> {}

  sealed interface Context permits Context.Input, Context.Created, Context.Associated {
    record Input(String name, AccountId accountId) implements Context {}

    record Created(UserProfile userProfile, AccountId accountId) implements Context {}

    record Associated(UserProfile userProfile) implements Context {}
  }

  /**
   * ユーザープロファイル作成処理を実行します
   *
   * @param name ユーザー名
   * @param accountId アカウントID
   * @return 作成されたユーザープロファイルを含むMono
   */
  Mono<UserProfile> execute(String name, AccountId accountId);

  /** プロファイル作成失敗時のカスタム例外 */
  public static class UserProfileCreationException extends DomainException {
    public UserProfileCreationException(String message) {
      super(message);
    }
  }

  /** アカウントIDが既に関連付けられている場合のカスタム例外 */
  public static class AccountAlreadyAssociatedException extends DomainException {
    public AccountAlreadyAssociatedException(String accountId) {
      super("アカウントID: " + accountId + " は既にユーザープロファイルに関連付けられています");
    }
  }
}
