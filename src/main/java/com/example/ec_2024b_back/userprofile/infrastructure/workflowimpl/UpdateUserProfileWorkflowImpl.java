package com.example.ec_2024b_back.userprofile.infrastructure.workflowimpl;

import com.example.ec_2024b_back.userprofile.application.workflow.UpdateUserProfileWorkflow;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile.UserProfileId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** ユーザープロファイル更新ワークフローの実装 */
@Component
@Primary
@RequiredArgsConstructor
public class UpdateUserProfileWorkflowImpl implements UpdateUserProfileWorkflow {

  private final FindUserProfileByIdStep findUserProfileByIdStep;
  private final UpdateUserProfileStep updateUserProfileStep;

  @Override
  public Mono<UserProfile> execute(UserProfileId userProfileId, String name) {
    return Mono.just(new Context.Input(userProfileId, name))
        .flatMap(findUserProfileByIdStep)
        .flatMap(updateUserProfileStep)
        .map(Context.Updated::userProfile)
        .onErrorMap(
            e -> {
              if (e instanceof UserProfileNotFoundException) {
                return e;
              }
              return new UserProfileNotFoundException(userProfileId.toString());
            });
  }
}
