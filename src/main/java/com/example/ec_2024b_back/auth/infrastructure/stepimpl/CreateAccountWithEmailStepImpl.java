package com.example.ec_2024b_back.auth.infrastructure.stepimpl;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.EmailAuthentication;
import com.example.ec_2024b_back.auth.domain.models.EmailAuthentication.HashedPassword;
import com.example.ec_2024b_back.auth.domain.services.AccountFactory;
import com.example.ec_2024b_back.auth.domain.step.CreateAccountWithEmailStep;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** FindUserByEmailStepの実装クラス. */
@Component
@RequiredArgsConstructor
public class CreateAccountWithEmailStepImpl implements CreateAccountWithEmailStep {

  private final PasswordEncoder passwordEncoder;
  private final AccountFactory accountFactory;

  @Override
  public Mono<Account> apply(EmailWithPasswordInput input) {

    var hashedPassord = new HashedPassword(passwordEncoder.encode(input.rawPassword()));
    var emailAuthentication = new EmailAuthentication(input.account(), hashedPassord);
    return Mono.just(accountFactory.create(ImmutableList.of(emailAuthentication)));
  }
}
