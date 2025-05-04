package com.example.ec_2024b_back.userprofile.application.usecase;

import com.example.ec_2024b_back.userprofile.application.workflow.UpdateUserProfileWorkflow;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile.UserProfileId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/** ユーザープロファイル更新ユースケースを実装するクラス. */
@Service
@RequiredArgsConstructor
public class UpdateUserProfileUsecase {

  private final UpdateUserProfileWorkflow updateUserProfileWorkflow;
  private final ApplicationEventPublisher event;

  /**
   * ユーザープロファイル更新処理を実行し、成功時は更新されたプロファイルを、失敗時はエラーを発行するMonoを返します.
   *
   * @param userProfileId ユーザープロファイルID
   * @param name 更新後のユーザー名
   * @return 更新されたユーザープロファイルを含むMono
   */
  @Transactional
  public Mono<UserProfile> execute(String userProfileId, String name) {
    var id = UserProfileId.of(userProfileId);
    return updateUserProfileWorkflow
        .execute(id, name)
        .onErrorMap(
            e -> new ProfileUpdateFailedException("Failed to update profile: " + e.getMessage(), e))
        .doOnNext(profile -> profile.getDomainEvents().forEach(event::publishEvent));
  }

  /** プロファイル更新失敗を表すカスタム例外. */
  public static class ProfileUpdateFailedException extends RuntimeException {
    public ProfileUpdateFailedException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
