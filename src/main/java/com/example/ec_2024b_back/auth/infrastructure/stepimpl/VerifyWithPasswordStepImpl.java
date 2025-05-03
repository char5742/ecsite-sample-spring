package com.example.ec_2024b_back.auth.infrastructure.stepimpl;

import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow.InvalidPasswordException;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow.NoEmailAuthenticationException;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow.VerifyWithPasswordStep;
import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.EmailAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** VerifyPasswordStepの実装クラス */
@Component
@RequiredArgsConstructor
public class VerifyWithPasswordStepImpl implements VerifyWithPasswordStep {

  private final PasswordEncoder passwordEncoder;

  @Override
  public Mono<LoginWorkflow.Context.Verified> apply(LoginWorkflow.Context.Founded f) {
    return getEmailAuthentication(f.account())
        .map(EmailAuthentication::password)
        .switchIfEmpty(Mono.error(NoEmailAuthenticationException::new))
        .map(hashedPassword -> passwordEncoder.matches(f.rawPassword(), hashedPassword.value()))
        .flatMap(
            (var matches) -> {
              if (matches) {
                return Mono.just(new LoginWorkflow.Context.Verified(f.account()));
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
        .orElse(Mono.empty());
  }
}
