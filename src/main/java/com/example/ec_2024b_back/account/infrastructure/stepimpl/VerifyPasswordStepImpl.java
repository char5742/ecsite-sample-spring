package com.example.ec_2024b_back.account.infrastructure.stepimpl;

import com.example.ec_2024b_back.account.domain.step.PasswordInput;
import com.example.ec_2024b_back.account.domain.step.VerifyPasswordStep;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** VerifyPasswordStepの実装クラス. */
@Component
@RequiredArgsConstructor
public class VerifyPasswordStepImpl implements VerifyPasswordStep {

  private final PasswordEncoder passwordEncoder;

  @Override
  public String apply(PasswordInput input) {
    var accountId = input.accountId();
    var hashedPassword = input.hashedPassword();
    var rawPassword = input.rawPassword();

    var matches = passwordEncoder.matches(rawPassword, hashedPassword);

    if (matches) {
      return accountId;
    } else {
      throw new InvalidPasswordException();
    }
  }
}
