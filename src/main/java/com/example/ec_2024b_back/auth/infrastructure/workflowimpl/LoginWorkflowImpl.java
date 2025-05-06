package com.example.ec_2024b_back.auth.infrastructure.workflowimpl;

import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow.Context.AccountWithJwt;
import com.example.ec_2024b_back.share.domain.models.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoginWorkflowImpl implements LoginWorkflow {

  private final FindAccountByEmailStep findAccountByEmailStep;
  private final VerifyWithPasswordStep verifyWithPasswordStep;
  private final GenerateJWTStep generateJwtStep;

  @Override
  public Mono<AccountWithJwt> execute(Email email, String rawPassword) {
    return findAccountByEmailStep
        .apply(new Context.Input(email, rawPassword))
        .switchIfEmpty(Mono.error(new UserNotFoundException(email.value())))
        .flatMap(verifyWithPasswordStep)
        .flatMap(generateJwtStep);
  }
}
