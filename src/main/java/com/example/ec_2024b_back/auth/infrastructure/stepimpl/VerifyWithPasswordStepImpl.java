package com.example.ec_2024b_back.auth.infrastructure.stepimpl;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.EmailAuthentication;
import com.example.ec_2024b_back.auth.domain.step.VerifyWithPasswordStep;
import com.example.ec_2024b_back.auth.domain.step.VerifyWithPasswordStep.PasswordInput;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** VerifyPasswordStepの実装クラス. */
@Component
@RequiredArgsConstructor
public class VerifyWithPasswordStepImpl implements VerifyWithPasswordStep {

  private final PasswordEncoder passwordEncoder;

  @Override
  public Mono<Account> apply(PasswordInput input) {
    return getEmailAuthentication(input.account())
        .map(EmailAuthentication::password)
        .switchIfEmpty(Mono.error(NoEmailAuthenticationException::new))
        .map(hashedPassword -> passwordEncoder.matches(input.rawPassword(), hashedPassword.value()))
        .flatMap(
            (var matches) -> {
              if (matches) {
                return Mono.just(input.account());
              } else {
                return Mono.error(InvalidPasswordException::new);
              }
            });
  }

  private static Mono<EmailAuthentication> getEmailAuthentication(Account account) {

    return account.getAuthentications().stream()
        .filter(auth -> auth instanceof EmailAuthentication)
        .map(EmailAuthentication.class::cast)
        .findFirst()
        .map(Mono::just)
        .orElseGet(Mono::empty);
  }
}
