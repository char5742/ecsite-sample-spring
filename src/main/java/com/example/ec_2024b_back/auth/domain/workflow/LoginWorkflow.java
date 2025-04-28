package com.example.ec_2024b_back.auth.domain.workflow;

import com.example.ec_2024b_back.auth.domain.models.JsonWebToken;
import com.example.ec_2024b_back.auth.domain.step.FindAccountByEmailStep;
import com.example.ec_2024b_back.auth.domain.step.GenerateJWTStep;
import com.example.ec_2024b_back.auth.domain.step.VerifyWithPasswordStep;
import com.example.ec_2024b_back.auth.domain.step.VerifyWithPasswordStep.PasswordInput;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.share.domain.models.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** ログイン処理を実行するワークフロー. */
@Component
@RequiredArgsConstructor
public class LoginWorkflow {

  private final FindAccountByEmailStep findAccountByEmailStep;
  private final VerifyWithPasswordStep verifyWithPasswordStep;
  private final GenerateJWTStep generateJwtStep;

  /**
   * ログイン処理を実行します.
   *
   * @param email メールアドレス
   * @param rawPassword 生パスワード
   * @return JWTトークンを含むMono (成功時はトークン、失敗時はエラー)
   */
  public Mono<JsonWebToken> execute(String email, String rawPassword) {
    return findAccountByEmailStep
        .apply(new Email(email))
        // ユーザー存在チェック
        .switchIfEmpty(Mono.error(new UserNotFoundException(email)))
        .map(a -> new PasswordInput(a, rawPassword))
        // パスワード検証
        .flatMap(verifyWithPasswordStep)
        // JWTトークン生成
        .flatMap(generateJwtStep);
  }

  /**
   * ログイン失敗時にエラーを生成します.
   *
   * @param email ログインに使用されたメールアドレス
   * @return エラーを含むMono
   */
  public Mono<String> createUserNotFoundError(String email) {
    return Mono.error(new UserNotFoundException(email));
  }

  /** ユーザーが見つからない場合のカスタム例外. */
  public static class UserNotFoundException extends DomainException {
    public UserNotFoundException(String email) {
      super("メールアドレス: " + email + " のユーザーが見つかりません");
    }
  }
}
