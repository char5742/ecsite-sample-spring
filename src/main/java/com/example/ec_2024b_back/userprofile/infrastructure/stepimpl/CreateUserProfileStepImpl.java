package com.example.ec_2024b_back.userprofile.infrastructure.stepimpl;

import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.userprofile.application.workflow.CreateUserProfileWorkflow;
import com.example.ec_2024b_back.userprofile.application.workflow.CreateUserProfileWorkflow.CreateUserProfileStep;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import com.example.ec_2024b_back.userprofile.domain.repositories.UserProfiles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** ユーザープロファイル作成ステップの実装 */
@Component
@Primary
@RequiredArgsConstructor
public class CreateUserProfileStepImpl implements CreateUserProfileStep {

  private final UserProfiles userProfiles;
  private final IdGenerator idGenerator;

  @Override
  public Mono<CreateUserProfileWorkflow.Context.Created> apply(
      CreateUserProfileWorkflow.Context.Input input) {
    return Mono.fromCallable(() -> UserProfile.create(idGenerator.newId(), input.name()))
        .flatMap(userProfiles::save)
        .map(
            userProfile ->
                new CreateUserProfileWorkflow.Context.Created(userProfile, input.accountId()))
        .onErrorMap(
            e ->
                new CreateUserProfileWorkflow.UserProfileCreationException(
                    "プロファイル作成中のエラー: " + e.getMessage()));
  }
}
