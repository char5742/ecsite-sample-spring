package com.example.ec_2024b_back.auth.infrastructure.workflowimpl;

import com.example.ec_2024b_back.auth.application.workflow.SignupWorkflow;
import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.share.domain.models.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SignupWorkflowImpl implements SignupWorkflow {

  private final CheckExistsEmailStep checkExistsEmailStep;
  private final CreateAccountWithEmailStep createAccountWithEmailStep;

  @Override
  public Mono<Account> execute(Email email, String password) {
    return checkExistsEmailStep
        .apply(new Context.Input(email, password))
        .flatMap(createAccountWithEmailStep)
        .map(Context.Created::account);
  }
}
