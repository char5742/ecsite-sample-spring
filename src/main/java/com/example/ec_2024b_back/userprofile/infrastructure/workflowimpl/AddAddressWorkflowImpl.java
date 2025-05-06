package com.example.ec_2024b_back.userprofile.infrastructure.workflowimpl;

import com.example.ec_2024b_back.userprofile.application.workflow.AddAddressWorkflow;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile.UserProfileId;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 住所追加ワークフローの実装クラス */
@Component
@Primary
@RequiredArgsConstructor
public class AddAddressWorkflowImpl implements AddAddressWorkflow {

  private final FindUserProfileByIdStep findUserProfileByIdStep;
  private final CreateAddressStep createAddressStep;
  private final AddAddressToUserProfileStep addAddressToUserProfileStep;

  @Override
  public Mono<UserProfile> execute(
      UserProfileId userProfileId,
      String name,
      String postalCode,
      String prefecture,
      String city,
      String street,
      @Nullable String building,
      String phoneNumber,
      boolean isDefault) {

    return Mono.just(
            new Context.Input(
                userProfileId,
                name,
                postalCode,
                prefecture,
                city,
                street,
                building,
                phoneNumber,
                isDefault))
        .flatMap(findUserProfileByIdStep)
        .map(createAddressStep)
        .flatMap(addAddressToUserProfileStep)
        .map(Context.AddressAdded::userProfile)
        .onErrorMap(
            e -> {
              if (e instanceof UserProfileNotFoundException) {
                return e;
              }
              return new UserProfileNotFoundException(userProfileId.toString());
            });
  }
}
