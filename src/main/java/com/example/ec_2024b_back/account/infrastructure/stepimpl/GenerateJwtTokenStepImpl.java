package com.example.ec_2024b_back.account.infrastructure.stepimpl;

import com.example.ec_2024b_back.account.domain.step.GenerateJwtTokenStep;
import com.example.ec_2024b_back.share.infrastructure.security.JsonWebTokenProvider;
import com.example.ec_2024b_back.user.domain.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** GenerateJwtTokenStepの実装クラス. */
@Component
@RequiredArgsConstructor
public class GenerateJwtTokenStepImpl implements GenerateJwtTokenStep {

  private final JsonWebTokenProvider jsonWebTokenProvider;

  @Override
  public String apply(User user) {
    return jsonWebTokenProvider.generateToken(user);
  }
}
