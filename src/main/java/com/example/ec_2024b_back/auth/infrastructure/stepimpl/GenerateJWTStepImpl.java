package com.example.ec_2024b_back.auth.infrastructure.stepimpl;

import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow.GenerateJWTStep;
import com.example.ec_2024b_back.auth.domain.models.JsonWebToken;
import com.example.ec_2024b_back.auth.infrastructure.security.JsonWebTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** GenerateJWTStepの実装クラス. */
@Component
@RequiredArgsConstructor
public class GenerateJWTStepImpl implements GenerateJWTStep {

  private final JsonWebTokenProvider jsonWebTokenProvider;

  @Override
  public Mono<LoginWorkflow.Context.AccountWithJwt> apply(LoginWorkflow.Context.Verified v) {
    return Mono.just(new JsonWebToken(jsonWebTokenProvider.generateToken(v.account())))
        .map(jwt -> new LoginWorkflow.Context.AccountWithJwt(v.account(), jwt))
        .onErrorMap(throwable -> new LoginWorkflow.InvalidPasswordException());
  }
}
