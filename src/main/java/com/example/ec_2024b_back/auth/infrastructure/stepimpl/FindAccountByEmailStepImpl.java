package com.example.ec_2024b_back.auth.infrastructure.stepimpl;

import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow.FindAccountByEmailStep;
import com.example.ec_2024b_back.auth.domain.repositories.Accounts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** FindUserByEmailStepの実装クラス. */
@Component
@RequiredArgsConstructor
public class FindAccountByEmailStepImpl implements FindAccountByEmailStep {

  private final Accounts accounts;

  @Override
  public Mono<LoginWorkflow.Context.Founded> apply(LoginWorkflow.Context.Input i) {
    return accounts
        .findByEmail(i.email())
        .map(a -> new LoginWorkflow.Context.Founded(a, i.rawPassword()))
        .switchIfEmpty(Mono.error(new LoginWorkflow.UserNotFoundException(i.email().value())))
        .onErrorMap(_ -> new LoginWorkflow.UserNotFoundException(i.email().value()));
  }
}
