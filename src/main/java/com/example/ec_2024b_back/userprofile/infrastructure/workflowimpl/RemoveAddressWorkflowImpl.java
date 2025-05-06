package com.example.ec_2024b_back.userprofile.infrastructure.workflowimpl;

import com.example.ec_2024b_back.userprofile.application.workflow.RemoveAddressWorkflow;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile.UserProfileId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 住所削除ワークフローの実装 */
@Component
@Primary
@RequiredArgsConstructor
public class RemoveAddressWorkflowImpl implements RemoveAddressWorkflow {

  private final FindUserProfileByIdStep findUserProfileByIdStep;
  private final RemoveAddressFromUserProfileStep removeAddressFromUserProfileStep;

  @Override
  public Mono<UserProfile> execute(UserProfileId userProfileId, String addressId) {
    return Mono.just(new Context.Input(userProfileId, addressId))
        .flatMap(findUserProfileByIdStep)
        .flatMap(removeAddressFromUserProfileStep)
        .map(Context.AddressRemoved::userProfile)
        .onErrorMap(
            e -> {
              if (e instanceof UserProfileNotFoundException
                  || e instanceof AddressNotFoundException) {
                return e;
              }
              return new UserProfileNotFoundException(userProfileId.toString());
            });
  }
}
