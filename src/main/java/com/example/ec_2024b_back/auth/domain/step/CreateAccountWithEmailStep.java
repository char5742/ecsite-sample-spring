package com.example.ec_2024b_back.auth.domain.step;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.share.domain.models.Email;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/**
 * アカウントを取得するステップを表す関数型インターフェース.
 *
 * <p>Input: Email (メールアドレス)
 *
 * <p>Output: Account (アカウント情報)
 */
@FunctionalInterface
public interface CreateAccountWithEmailStep
    extends Function<CreateAccountWithEmailStep.EmailWithPasswordInput, Mono<Account>> {
  record EmailWithPasswordInput(Email account, String rawPassword) {}
}
