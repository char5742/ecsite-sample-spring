package com.example.ec_2024b_back.auth.infrastructure.stepimpl;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.JsonWebToken;
import com.example.ec_2024b_back.auth.domain.step.GenerateJWTStep;
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
  public Mono<JsonWebToken> apply(Account t) {
    return Mono.just(new JsonWebToken(jsonWebTokenProvider.generateToken(t)));
  }
}
