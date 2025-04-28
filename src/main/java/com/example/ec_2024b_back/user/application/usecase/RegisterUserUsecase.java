package com.example.ec_2024b_back.user.application.usecase;

import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.user.domain.workflow.RegisterUserWorkflow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/** ユーザー登録ユースケースを実装するクラス. */
@Service
@RequiredArgsConstructor
public class RegisterUserUsecase {

  private final RegisterUserWorkflow registerUserWorkflow;

  /** ユーザー登録成功時のレスポンスDTO. */
  public record RegisterUserSuccessDto(String userId, String fullName) {
    public static RegisterUserSuccessDto fromUser(User user) {
      return new RegisterUserSuccessDto(user.id().id(), user.getFullName());
    }
  }

  /**
   * ユーザー登録処理を実行し、成功時はユーザーID・名前を含むDTO、失敗時はエラーを発行するMonoを返します.
   *
   * @param firstName 名前（名）
   * @param lastName 名前（姓）
   * @param email メールアドレス
   * @param password 平文パスワード
   * @param address 住所
   * @param telephone 電話番号
   * @return 登録結果を含むMono
   */
  public Mono<RegisterUserSuccessDto> execute(
      String firstName,
      String lastName,
      String email,
      String password,
      Address address,
      String telephone) {

    return registerUserWorkflow
        .execute(firstName, lastName, email, password, address, telephone)
        .map(RegisterUserSuccessDto::fromUser)
        .onErrorMap(RegistrationFailedException::new);
  }

  /** 登録失敗を表すカスタム例外. */
  public static class RegistrationFailedException extends RuntimeException {
    public RegistrationFailedException(Throwable cause) {
      super("ユーザー登録に失敗しました: " + cause.getMessage(), cause);
    }
  }
}
