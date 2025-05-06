package com.example.ec_2024b_back.userprofile.infrastructure.stepimpl;

import com.example.ec_2024b_back.userprofile.application.workflow.UpdateUserProfileWorkflow;
import com.example.ec_2024b_back.userprofile.application.workflow.UpdateUserProfileWorkflow.FindUserProfileByIdStep;
import com.example.ec_2024b_back.userprofile.application.workflow.UpdateUserProfileWorkflow.UserProfileNotFoundException;
import com.example.ec_2024b_back.userprofile.domain.repositories.UserProfiles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** プロファイル更新のためのユーザープロファイル検索ステップの実装 */
@Component
@Primary
@RequiredArgsConstructor
public class FindUserProfileByIdForUpdateStepImpl implements FindUserProfileByIdStep {

  private final UserProfiles userProfiles;

  @Override
  public Mono<UpdateUserProfileWorkflow.Context.Found> apply(
      UpdateUserProfileWorkflow.Context.Input input) {
    return userProfiles
        .findById(input.userProfileId())
        .switchIfEmpty(
            Mono.error(new UserProfileNotFoundException(input.userProfileId().toString())))
        .map(userProfile -> new UpdateUserProfileWorkflow.Context.Found(userProfile, input.name()));
  }
}
