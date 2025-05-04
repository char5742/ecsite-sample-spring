package com.example.ec_2024b_back.userprofile.application.usecase;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.userprofile.application.workflow.CreateUserProfileWorkflow;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/** ユーザープロファイル作成ユースケースを実装するクラス. */
@Service
@RequiredArgsConstructor
public class CreateUserProfileUsecase {

  private final CreateUserProfileWorkflow createUserProfileWorkflow;
  private final ApplicationEventPublisher event;

  /**
   * ユーザープロファイル作成処理を実行し、成功時はプロファイルを、失敗時はエラーを発行するMonoを返します.
   *
   * @param accountId アカウントID
   * @param name ユーザー名
   * @return 新しいユーザープロファイルを含むMono
   */
  @Transactional
  public Mono<UserProfile> execute(AccountId accountId, String name) {
    return createUserProfileWorkflow
        .execute(name, accountId)
        .onErrorMap(
            e ->
                new ProfileCreationFailedException(
                    "Failed to create profile: " + e.getMessage(), e))
        .doOnNext(profile -> profile.getDomainEvents().forEach(event::publishEvent));
  }

  /** プロファイル作成失敗を表すカスタム例外. */
  public static class ProfileCreationFailedException extends RuntimeException {
    public ProfileCreationFailedException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
