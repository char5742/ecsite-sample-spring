package com.example.ec_2024b_back.userprofile.infrastructure.workflowimpl;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.userprofile.application.workflow.CreateUserProfileWorkflow;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** ユーザープロファイル作成ワークフローの実装クラス */
@Component
@Primary
@RequiredArgsConstructor
public class CreateUserProfileWorkflowImpl implements CreateUserProfileWorkflow {

  private final CreateUserProfileStep createUserProfileStep;
  private final AssociateWithAccountStep associateWithAccountStep;

  @Override
  public Mono<UserProfile> execute(String name, AccountId accountId) {
    return Mono.just(new Context.Input(name, accountId))
        .flatMap(createUserProfileStep)
        .flatMap(associateWithAccountStep)
        .map(Context.Associated::userProfile)
        .onErrorMap(
            e -> {
              if (e instanceof UserProfileCreationException
                  || e instanceof AccountAlreadyAssociatedException) {
                return e;
              }
              return new UserProfileCreationException("ユーザープロファイルの作成に失敗しました: " + e.getMessage());
            });
  }
}
