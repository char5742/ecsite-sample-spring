package com.example.ec_2024b_back.auth.domain.services;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.Authentication;
import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFactory {
  private final IdGenerator idGen;

  public Account create(List<Authentication> auths) {
    return Account.create(idGen.newId(), auths);
  }
}
