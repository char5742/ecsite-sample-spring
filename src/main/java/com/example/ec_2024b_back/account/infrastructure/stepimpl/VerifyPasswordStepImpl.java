package com.example.ec_2024b_back.account.infrastructure.stepimpl;

import com.example.ec_2024b_back.account.domain.step.VerifyPasswordStep;
import io.vavr.Tuple3;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** VerifyPasswordStepの実装クラス. */
@Component
@RequiredArgsConstructor
public class VerifyPasswordStepImpl implements VerifyPasswordStep {

  private final PasswordEncoder passwordEncoder;

  @Override
  public Try<String> apply(Tuple3<String, String, String> input) {
    var accountId = input._1;
    var hashedPassword = input._2;
    var rawPassword = input._3;

    var matches = passwordEncoder.matches(rawPassword, hashedPassword);

    if (matches) {
      return Try.success(accountId);
    } else {
      return Try.failure(new InvalidPasswordException());
    }
  }
}
