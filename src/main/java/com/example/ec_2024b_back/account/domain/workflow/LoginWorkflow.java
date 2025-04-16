package com.example.ec_2024b_back.account.domain.workflow;

import com.example.ec_2024b_back.account.domain.step.GenerateJwtTokenStep;
import com.example.ec_2024b_back.account.domain.step.PasswordInput;
import com.example.ec_2024b_back.account.domain.step.VerifyPasswordStep;
import com.example.ec_2024b_back.user.domain.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** ログイン処理を実行するワークフロー. */
@Component
@RequiredArgsConstructor
public class LoginWorkflow {

  private final VerifyPasswordStep verifyPasswordStep;
  private final GenerateJwtTokenStep generateJwtTokenStep;

  /**
   * ログイン処理を実行します.
   *
   * @param user ユーザーエンティティ
   * @param rawPassword 生パスワード
   * @return JWTトークンを含むMono (成功時はトークン、失敗時はエラー)
   */
  public Mono<String> execute(User user, String rawPassword) {
    return Mono.defer(
        () ->
            Mono.just(new PasswordInput(user.id().id(), user.password(), rawPassword))
                .map(verifyPasswordStep)
                .then(Mono.just(user).map(generateJwtTokenStep)));
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
  public static class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
      super("メールアドレス: " + email + " のユーザーが見つかりません");
    }
  }
}
