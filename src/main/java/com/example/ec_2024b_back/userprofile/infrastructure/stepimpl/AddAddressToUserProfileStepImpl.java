package com.example.ec_2024b_back.userprofile.infrastructure.stepimpl;

import com.example.ec_2024b_back.userprofile.application.workflow.AddAddressWorkflow;
import com.example.ec_2024b_back.userprofile.application.workflow.AddAddressWorkflow.AddAddressToUserProfileStep;
import com.example.ec_2024b_back.userprofile.domain.repositories.UserProfiles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** ユーザープロファイルに住所を追加するステップの実装 */
@Component
@Primary
@RequiredArgsConstructor
public class AddAddressToUserProfileStepImpl implements AddAddressToUserProfileStep {

  private final UserProfiles userProfiles;

  @Override
  public Mono<AddAddressWorkflow.Context.AddressAdded> apply(
      AddAddressWorkflow.Context.AddressCreated addressCreated) {
    // ユーザープロファイルに住所を追加
    var updatedProfile = addressCreated.userProfile().addAddress(addressCreated.address());

    // 更新したプロファイルを保存
    return userProfiles.save(updatedProfile).map(AddAddressWorkflow.Context.AddressAdded::new);
  }
}
