package com.example.ec_2024b_back.account.infrastructure.stepimpl;

import com.example.ec_2024b_back.account.domain.step.FindUserByEmailStep;
import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.user.infrastructure.repository.MongoUserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** FindUserByEmailStepの実装クラス. */
@Component
@RequiredArgsConstructor
public class FindUserByEmailStepImpl implements FindUserByEmailStep {

  private final MongoUserRepository userRepository;

  @Override
  public Mono<Optional<User>> apply(String email) {
    return userRepository.findByEmail(email);
  }
}
