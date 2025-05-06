package com.example.ec_2024b_back.userprofile.infrastructure.stepimpl;

import com.example.ec_2024b_back.userprofile.application.workflow.RemoveAddressWorkflow;
import com.example.ec_2024b_back.userprofile.application.workflow.RemoveAddressWorkflow.AddressNotFoundException;
import com.example.ec_2024b_back.userprofile.application.workflow.RemoveAddressWorkflow.RemoveAddressFromUserProfileStep;
import com.example.ec_2024b_back.userprofile.domain.models.AddressId;
import com.example.ec_2024b_back.userprofile.domain.repositories.UserProfiles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** ユーザープロファイルから住所を削除するステップの実装 */
@Component
@Primary
@RequiredArgsConstructor
public class RemoveAddressFromUserProfileStepImpl implements RemoveAddressFromUserProfileStep {

  private final UserProfiles userProfiles;

  @Override
  public Mono<RemoveAddressWorkflow.Context.AddressRemoved> apply(
      RemoveAddressWorkflow.Context.Found context) {
    try {
      // 住所IDを使用してプロファイルから住所を削除
      var addressId = AddressId.of(context.addressId());
      var updatedProfile = context.userProfile().removeAddress(addressId);

      // 更新したプロファイルを保存
      return userProfiles
          .save(updatedProfile)
          .map(RemoveAddressWorkflow.Context.AddressRemoved::new);
    } catch (IllegalArgumentException e) {
      // 住所が見つからない場合は専用の例外をスロー
      return Mono.error(new AddressNotFoundException(context.addressId()));
    }
  }
}
