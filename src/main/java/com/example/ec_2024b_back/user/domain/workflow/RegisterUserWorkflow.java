package com.example.ec_2024b_back.user.domain.workflow;

import com.example.ec_2024b_back.auth.domain.models.Account.AccountId;
import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.user.domain.repository.UserRepository;
import com.example.ec_2024b_back.user.domain.step.CheckEmailUniquenessStep;
import com.example.ec_2024b_back.user.domain.step.HashPasswordStep;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** ユーザー登録処理を実行するワークフロー. */
@Component
@RequiredArgsConstructor
public class RegisterUserWorkflow {

  private final UserRepository userRepository;
  private final CheckEmailUniquenessStep checkEmailUniquenessStep;
  private final HashPasswordStep hashPasswordStep;

  /**
   * ユーザー登録プロセスを実行します.
   *
   * @param firstName 名前（名）
   * @param lastName 名前（姓）
   * @param email メールアドレス
   * @param rawPassword 平文パスワード
   * @param address 住所
   * @param telephone 電話番号
   * @return 登録されたユーザー情報を含むMono
   */
  public Mono<User> execute(
      String firstName,
      String lastName,
      String email,
      String rawPassword,
      Address address,
      String telephone) {

    // 1. メールアドレスの一意性を確認
    return checkEmailUniquenessStep
        .apply(email)
        // 2. パスワードをハッシュ化
        .flatMap(validEmail -> hashPasswordStep.apply(rawPassword))
        // 3. ユーザーを作成して保存
        .flatMap(
            hashedPassword -> {
              // 新規ユーザーIDを作成
              var accountId = new AccountId(UUID.randomUUID().toString());
              // ユーザーオブジェクトを生成
              var newUser =
                  new User(accountId, firstName, lastName, address, telephone, hashedPassword);
              // ユーザーを保存（メールアドレスを含む）
              return userRepository.saveWithEmail(newUser, email);
            });
  }

  /** メールアドレスが既に使用されている場合のカスタム例外. */
  public static class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
      super("メールアドレス: " + email + " は既に使用されています");
    }
  }
}
