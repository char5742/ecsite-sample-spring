package com.example.ec_2024b_back.userprofile.infrastructure.stepimpl;

import com.example.ec_2024b_back.userprofile.application.workflow.UpdateUserProfileWorkflow;
import com.example.ec_2024b_back.userprofile.application.workflow.UpdateUserProfileWorkflow.UpdateUserProfileStep;
import com.example.ec_2024b_back.userprofile.domain.repositories.UserProfiles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** ユーザープロファイル更新ステップの実装 */
@Component
@Primary
@RequiredArgsConstructor
public class UpdateUserProfileStepImpl implements UpdateUserProfileStep {

  private final UserProfiles userProfiles;

  @Override
  public Mono<UpdateUserProfileWorkflow.Context.Updated> apply(
      UpdateUserProfileWorkflow.Context.Found context) {
    try {
      // プロファイル名を更新
      var updatedProfile = context.userProfile().updateName(context.name());

      // 更新したプロファイルを保存
      return userProfiles.save(updatedProfile).map(UpdateUserProfileWorkflow.Context.Updated::new);
    } catch (IllegalArgumentException e) {
      // 名前が不正な場合はエラーを返す
      return Mono.error(e);
    }
  }
}
