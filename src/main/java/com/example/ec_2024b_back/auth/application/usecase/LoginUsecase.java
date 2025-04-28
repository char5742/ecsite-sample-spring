package com.example.ec_2024b_back.auth.application.usecase;

import com.example.ec_2024b_back.auth.domain.models.JsonWebToken;
import com.example.ec_2024b_back.auth.domain.workflow.LoginWorkflow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/** ログインユースケースを実装するクラス. */
@Service
@RequiredArgsConstructor
public class LoginUsecase {

  private final LoginWorkflow loginWorkflow;

  /**
   * ログイン処理を実行し、成功時はJWTトークンを、失敗時はエラーを発行するMonoを返します.
   *
   * @return ログイン結果を含むMono
   */
  public Mono<JsonWebToken> execute(String email, String password) {

    // ドメイン層のワークフローを呼び出して認証とJWT生成を実行
    return loginWorkflow.execute(email, password).onErrorMap(AuthenticationFailedException::new);
  }

  /** 認証失敗を表すカスタム例外. */
  public static class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(Throwable cause) {
      super("Authentication failed: " + cause.getMessage(), cause);
    }
  }
}
