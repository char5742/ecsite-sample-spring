package com.example.ec_2024b_back.user.infrastructure.stepimpl;

import com.example.ec_2024b_back.user.domain.step.HashPasswordStep;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** HashPasswordStepの実装クラス. */
@Component
@RequiredArgsConstructor
public class HashPasswordStepImpl implements HashPasswordStep {

  private final PasswordEncoder passwordEncoder;

  @Override
  public Mono<String> apply(String rawPassword) {
    // パスワードをハッシュ化
    return Mono.fromCallable(() -> passwordEncoder.encode(rawPassword));
  }
}
