package com.example.ec_2024b_back.account.application.usecase;

import com.example.ec_2024b_back.account.domain.workflow.LoginWorkflow;
import com.example.ec_2024b_back.model.LoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/** ログインユースケースを実装するクラス. */
@Service
@RequiredArgsConstructor
public class LoginUsecase {

  private final LoginWorkflow loginWorkflow;

  /** ログイン成功時のレスポンスDTO. */
  public record LoginSuccessDto(String token) {}

  /**
   * ログイン処理を実行し、成功時はJWTトークンを含むDTO、失敗時はエラーを発行するMonoを返します.
   *
   * @param loginDto ログイン情報を含むDTO
   * @return ログイン結果を含むMono
   */
  public Mono<LoginSuccessDto> execute(LoginDto loginDto) {
    var email = loginDto.getEmail();
    var password = loginDto.getPassword();

    // ドメイン層のワークフローを呼び出して認証とJWT生成を実行
    return loginWorkflow
        .execute(email, password)
        .map(LoginSuccessDto::new)
        .onErrorMap(AuthenticationFailedException::new);
  }

  /** 認証失敗を表すカスタム例外. */
  public static class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(Throwable cause) {
      super("Authentication failed: " + cause.getMessage(), cause);
    }
  }
}
