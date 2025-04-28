package com.example.ec_2024b_back.auth.domain.repositories;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.share.domain.models.Email;
import reactor.core.publisher.Mono;

public interface AccountRepository {
  // メールアドレスでアカウントを検索するメソッド
  Mono<Account> findByEmail(Email email);

  // アカウントを保存するメソッド
  Mono<Account> save(Account account);
}
