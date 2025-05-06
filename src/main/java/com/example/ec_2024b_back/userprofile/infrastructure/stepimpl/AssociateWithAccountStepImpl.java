package com.example.ec_2024b_back.userprofile.infrastructure.stepimpl;

import com.example.ec_2024b_back.userprofile.application.workflow.CreateUserProfileWorkflow;
import com.example.ec_2024b_back.userprofile.application.workflow.CreateUserProfileWorkflow.AssociateWithAccountStep;
import com.example.ec_2024b_back.userprofile.domain.repositories.UserProfiles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** プロファイルとアカウントの関連付けステップの実装 */
@Component
@Primary
@RequiredArgsConstructor
public class AssociateWithAccountStepImpl implements AssociateWithAccountStep {

  private final UserProfiles userProfiles;

  @Override
  public Mono<CreateUserProfileWorkflow.Context.Associated> apply(
      CreateUserProfileWorkflow.Context.Created input) {
    // アカウントIDでプロファイルを検索し、既に存在する場合はエラーを返す
    return userProfiles
        .findByAccountId(input.accountId())
        .flatMap(
            existingProfile ->
                Mono.error(
                    new CreateUserProfileWorkflow.AccountAlreadyAssociatedException(
                        input.accountId().toString())))
        .then(Mono.just(new CreateUserProfileWorkflow.Context.Associated(input.userProfile())));
  }
}
