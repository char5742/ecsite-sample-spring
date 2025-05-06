package com.example.ec_2024b_back.userprofile.infrastructure.stepimpl;

import com.example.ec_2024b_back.userprofile.application.workflow.AddAddressWorkflow;
import com.example.ec_2024b_back.userprofile.application.workflow.AddAddressWorkflow.FindUserProfileByIdStep;
import com.example.ec_2024b_back.userprofile.application.workflow.AddAddressWorkflow.UserProfileNotFoundException;
import com.example.ec_2024b_back.userprofile.domain.repositories.UserProfiles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** ユーザープロファイル検索ステップの実装 */
@Component
@Primary
@RequiredArgsConstructor
public class FindUserProfileByIdStepImpl implements FindUserProfileByIdStep {

  private final UserProfiles userProfiles;

  @Override
  public Mono<AddAddressWorkflow.Context.Found> apply(AddAddressWorkflow.Context.Input input) {
    return userProfiles
        .findById(input.userProfileId())
        .switchIfEmpty(
            Mono.error(new UserProfileNotFoundException(input.userProfileId().toString())))
        .map(
            userProfile ->
                new AddAddressWorkflow.Context.Found(
                    userProfile,
                    input.name(),
                    input.postalCode(),
                    input.prefecture(),
                    input.city(),
                    input.street(),
                    input.building(),
                    input.phoneNumber(),
                    input.isDefault()));
  }
}
