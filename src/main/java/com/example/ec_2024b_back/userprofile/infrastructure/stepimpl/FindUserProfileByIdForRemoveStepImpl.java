package com.example.ec_2024b_back.userprofile.infrastructure.stepimpl;

import com.example.ec_2024b_back.userprofile.application.workflow.RemoveAddressWorkflow;
import com.example.ec_2024b_back.userprofile.application.workflow.RemoveAddressWorkflow.FindUserProfileByIdStep;
import com.example.ec_2024b_back.userprofile.application.workflow.RemoveAddressWorkflow.UserProfileNotFoundException;
import com.example.ec_2024b_back.userprofile.domain.repositories.UserProfiles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 住所削除のためのユーザープロファイル検索ステップの実装 */
@Component
@Primary
@RequiredArgsConstructor
public class FindUserProfileByIdForRemoveStepImpl implements FindUserProfileByIdStep {

  private final UserProfiles userProfiles;

  @Override
  public Mono<RemoveAddressWorkflow.Context.Found> apply(
      RemoveAddressWorkflow.Context.Input input) {
    return userProfiles
        .findById(input.userProfileId())
        .switchIfEmpty(
            Mono.error(new UserProfileNotFoundException(input.userProfileId().toString())))
        .map(
            userProfile -> new RemoveAddressWorkflow.Context.Found(userProfile, input.addressId()));
  }
}
