package com.example.ec_2024b_back.auth.infrastructure.stepimpl;

import com.example.ec_2024b_back.auth.application.workflow.SignupWorkflow;
import com.example.ec_2024b_back.auth.application.workflow.SignupWorkflow.CheckExistsEmailStep;
import com.example.ec_2024b_back.auth.domain.repositories.Accounts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** ユーザーのメールアドレスが既に登録済みかをチェックするステップの実装. */
@Component
@RequiredArgsConstructor
public class CheckExistsEmailStepImpl implements CheckExistsEmailStep {

  private final Accounts accounts;

  @Override
  public Mono<SignupWorkflow.Context.Checked> apply(SignupWorkflow.Context.Input context) {
    return accounts
        .findByEmail(context.email())
        .flatMap(
            account -> Mono.error(new SignupWorkflow.EmailAlreadyExistsException(context.email())))
        .then(
            Mono.just(new SignupWorkflow.Context.Checked(context.email(), context.rawPassword())));
  }
}
