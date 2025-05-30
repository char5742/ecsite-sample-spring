package com.example.ec_2024b_back.auth.infrastructure.stepimpl;

import com.example.ec_2024b_back.auth.application.workflow.SignupWorkflow;
import com.example.ec_2024b_back.auth.application.workflow.SignupWorkflow.CreateAccountWithEmailStep;
import com.example.ec_2024b_back.auth.domain.models.Authentication;
import com.example.ec_2024b_back.auth.domain.models.EmailAuthentication;
import com.example.ec_2024b_back.auth.domain.models.EmailAuthentication.HashedPassword;
import com.example.ec_2024b_back.auth.domain.repositories.Accounts;
import com.example.ec_2024b_back.auth.domain.services.AccountFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** メールアドレスとパスワードでアカウントを作成するステップの実装. */
@Component
@Primary
@RequiredArgsConstructor
public class CreateAccountWithEmailStepImpl implements CreateAccountWithEmailStep {

  private final AccountFactory accountFactory;
  private final PasswordEncoder passwordEncoder;
  private final Accounts accounts;

  @Override
  public Mono<SignupWorkflow.Context.Created> apply(SignupWorkflow.Context.Checked context) {
    // パスワードをハッシュ化
    String hashedPassword = passwordEncoder.encode(context.rawPassword());

    // 認証情報を作成
    Authentication auth =
        new EmailAuthentication(context.email(), new HashedPassword(hashedPassword));

    // アカウント作成
    var account = accountFactory.create(List.of(auth));

    // アカウントを保存して返す
    return accounts
        .save(account)
        .map(savedAccount -> new SignupWorkflow.Context.Created(savedAccount));
  }
}
