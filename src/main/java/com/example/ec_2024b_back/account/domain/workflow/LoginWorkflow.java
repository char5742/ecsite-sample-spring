package com.example.ec_2024b_back.account.domain.workflow;

import com.example.ec_2024b_back.account.domain.step.GenerateJwtTokenStep;
import com.example.ec_2024b_back.account.domain.step.VerifyPasswordStep;
import com.example.ec_2024b_back.user.domain.repository.UserRepository;
import io.vavr.Tuple;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** ログイン処理を実行するワークフロー. */
@Component
@RequiredArgsConstructor
public class LoginWorkflow {

  private final VerifyPasswordStep verifyPasswordStep;
  private final GenerateJwtTokenStep generateJwtTokenStep;
  private final UserRepository userRepository;

  /**
   * ログイン処理を実行します.
   *
   * @param email メールアドレス
   * @param rawPassword 生パスワード
   * @return JWTトークンを含むMono (成功時 Try.success(token), 失敗時 Try.failure(exception))
   */
  public Mono<Try<String>> execute(String email, String rawPassword) {
    return userRepository
        .findDocumentByEmail(email)
        .flatMap(
            userDoc -> {
              var verifiedAccountIdTry =
                  verifyPasswordStep.apply(
                      Tuple.of(userDoc.getId(), userDoc.getPassword(), rawPassword));

              return verifiedAccountIdTry
                  .map(
                      accountId -> {
                        // UserDocumentから直接ドメインモデルに変換
                        var user = userDoc.toDomain();
                        return generateJwtTokenStep.apply(user);
                      })
                  .map(Mono::just) // 結果をMonoでラップ
                  .getOrElseGet(Mono::error);
            })
        .switchIfEmpty(Mono.error(new UserNotFoundException(email)));
  }

  /** ユーザーが見つからない場合のカスタム例外. */
  public static class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
      super("メールアドレス: " + email + " のユーザーが見つかりません");
    }
  }

  // convertToDomainメソッドは削除
}
