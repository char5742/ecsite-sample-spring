package com.example.ec_2024b_back.user.infrastructure.stepimpl;

import com.example.ec_2024b_back.user.domain.repository.UserRepository;
import com.example.ec_2024b_back.user.domain.step.CheckEmailUniquenessStep;
import com.example.ec_2024b_back.user.domain.workflow.RegisterUserWorkflow.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** CheckEmailUniquenessStepの実装クラス. */
@Component
@RequiredArgsConstructor
public class CheckEmailUniquenessStepImpl implements CheckEmailUniquenessStep {

  private final UserRepository userRepository;

  @Override
  public Mono<String> apply(String email) {
    return userRepository
        .findByEmail(email)
        .flatMap(
            optionalUser -> {
              // メールアドレスが存在する場合はエラー
              if (optionalUser.isPresent()) {
                return Mono.error(new EmailAlreadyExistsException(email));
              }
              // 存在しない場合はメールアドレスを返す
              return Mono.just(email);
            });
  }
}
