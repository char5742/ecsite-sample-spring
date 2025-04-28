package com.example.ec_2024b_back.auth.infrastructure.stepimpl;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.repositories.AccountRepository;
import com.example.ec_2024b_back.auth.domain.step.FindAccountByEmailStep;
import com.example.ec_2024b_back.share.domain.models.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** FindUserByEmailStepの実装クラス. */
@Component
@RequiredArgsConstructor
public class FindAccountByEmailStepImpl implements FindAccountByEmailStep {

  private final AccountRepository accountRepository;

  @Override
  public Mono<Account> apply(Email email) {
    return accountRepository.findByEmail(email);
  }
}
