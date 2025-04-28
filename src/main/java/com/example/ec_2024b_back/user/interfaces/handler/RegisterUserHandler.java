package com.example.ec_2024b_back.user.interfaces.handler;

import com.example.ec_2024b_back.model.UserRegistrationDto;
import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.user.application.usecase.RegisterUserUsecase;
import com.example.ec_2024b_back.user.domain.workflow.RegisterUserWorkflow.EmailAlreadyExistsException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** ユーザー登録リクエストを処理するハンドラー. */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterUserHandler {

  private final RegisterUserUsecase registerUserUsecase;

  /** POST /api/registration リクエストを処理し、ユーザー登録を実行. */
  public Mono<ServerResponse> registerUser(ServerRequest request) {
    return request
        .bodyToMono(UserRegistrationDto.class)
        .flatMap(
            dto -> {
              log.info("ユーザー登録リクエストを受信: email={}", dto.getEmail()); // パスワードなどの機密情報はログに出力しない

              // 入力値の基本的なバリデーションを実施
              String validationError = validateRegistrationDto(dto);
              if (validationError != null) {
                log.warn("ユーザー登録バリデーションエラー: email={}, error={}", dto.getEmail(), validationError);
                return ServerResponse.badRequest()
                    .bodyValue(new ErrorResponse("VALIDATION_ERROR", validationError));
              }

              // アドレスオブジェクトを作成
              Address address;
              try {
                address = createAddressFromDto(dto);
              } catch (IllegalArgumentException e) {
                log.warn("住所情報の変換エラー: email={}, error={}", dto.getEmail(), e.getMessage());
                return ServerResponse.badRequest()
                    .bodyValue(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
              }

              // ユースケースを実行
              return registerUserUsecase
                  .execute(
                      dto.getFirstName(),
                      dto.getLastName(),
                      dto.getEmail(),
                      dto.getPassword(),
                      address,
                      dto.getTelephone())
                  .flatMap(
                      result -> {
                        log.info("ユーザー登録成功: userId={}, email={}", result.userId(), dto.getEmail());
                        return ServerResponse.status(HttpStatus.CREATED).bodyValue(result);
                      })
                  .onErrorResume(
                      EmailAlreadyExistsException.class,
                      e -> {
                        log.warn("メールアドレス重複エラー: email={}", dto.getEmail());
                        return ServerResponse.status(HttpStatus.CONFLICT)
                            .bodyValue(new ErrorResponse("EMAIL_ALREADY_EXISTS", e.getMessage()));
                      })
                  .onErrorResume(
                      RegisterUserUsecase.RegistrationFailedException.class,
                      e -> {
                        log.error(
                            "ユーザー登録に失敗しました: email={}, error={}", dto.getEmail(), e.getMessage());
                        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .bodyValue(new ErrorResponse("REGISTRATION_FAILED", "ユーザー登録に失敗しました"));
                      });
            });
  }

  /**
   * AppControllerから直接呼び出せるようにするためのメソッド。 UserRegistrationDtoを受け取り、ResponseEntityを返却します。
   *
   * @param dto ユーザー登録DTO
   * @return レスポンスを含むMono
   */
  public Mono<ResponseEntity<Object>> handleRegistration(UserRegistrationDto dto) {
    log.info("ユーザー登録リクエストを受信: email={}", dto.getEmail()); // パスワードなどの機密情報はログに出力しない

    // 入力値の基本的なバリデーションを実施
    String validationError = validateRegistrationDto(dto);
    if (validationError != null) {
      log.warn("ユーザー登録バリデーションエラー: email={}, error={}", dto.getEmail(), validationError);
      return Mono.just(
          ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(new ErrorResponse("VALIDATION_ERROR", validationError)));
    }

    // アドレスオブジェクトを作成
    Address address;
    try {
      address = createAddressFromDto(dto);
    } catch (IllegalArgumentException e) {
      log.warn("住所情報の変換エラー: email={}, error={}", dto.getEmail(), e.getMessage());
      return Mono.just(
          ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage())));
    }

    // ユースケースを実行
    return registerUserUsecase
        .execute(
            dto.getFirstName(),
            dto.getLastName(),
            dto.getEmail(),
            dto.getPassword(),
            address,
            dto.getTelephone())
        .map(
            result -> {
              log.info("ユーザー登録成功: userId={}, email={}", result.userId(), dto.getEmail());
              return ResponseEntity.status(HttpStatus.CREATED).body((Object) result);
            })
        .onErrorResume(
            EmailAlreadyExistsException.class,
            e -> {
              log.warn("メールアドレス重複エラー: email={}", dto.getEmail());
              return Mono.just(
                  ResponseEntity.status(HttpStatus.CONFLICT)
                      .body(new ErrorResponse("EMAIL_ALREADY_EXISTS", e.getMessage())));
            })
        .onErrorResume(
            RegisterUserUsecase.RegistrationFailedException.class,
            e -> {
              log.error("ユーザー登録に失敗しました: email={}, error={}", dto.getEmail(), e.getMessage());
              return Mono.just(
                  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                      .body(new ErrorResponse("REGISTRATION_FAILED", "ユーザー登録に失敗しました")));
            });
  }

  /**
   * UserRegistrationDtoのバリデーションを行います.
   *
   * @param dto 検証対象のDTO
   * @return 検証エラーメッセージ（検証に成功した場合はnull）
   */
  private static @Nullable String validateRegistrationDto(UserRegistrationDto dto) {
    if (Objects.isNull(dto.getFirstName()) || dto.getFirstName().isBlank()) {
      return "名は必須です";
    }
    if (Objects.isNull(dto.getLastName()) || dto.getLastName().isBlank()) {
      return "姓は必須です";
    }
    if (Objects.isNull(dto.getEmail()) || dto.getEmail().isBlank()) {
      return "メールアドレスは必須です";
    }
    if (!dto.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
      return "メールアドレスの形式が不正です";
    }
    if (Objects.isNull(dto.getPassword()) || dto.getPassword().isBlank()) {
      return "パスワードは必須です";
    }
    if (dto.getPassword().length() < 8) {
      return "パスワードは8文字以上である必要があります";
    }
    if (Objects.isNull(dto.getConfirmPassword()) || dto.getConfirmPassword().isBlank()) {
      return "確認用パスワードは必須です";
    }
    if (!dto.getPassword().equals(dto.getConfirmPassword())) {
      return "パスワードと確認用パスワードが一致しません";
    }
    if (Objects.isNull(dto.getZipcode()) || dto.getZipcode().isBlank()) {
      return "郵便番号は必須です";
    }
    if (Objects.isNull(dto.getPrefecture()) || dto.getPrefecture().isBlank()) {
      return "都道府県は必須です";
    }
    if (Objects.isNull(dto.getMunicipalities()) || dto.getMunicipalities().isBlank()) {
      return "市区町村は必須です";
    }
    if (Objects.isNull(dto.getAddress()) || dto.getAddress().isBlank()) {
      return "番地・建物名等は必須です";
    }
    if (Objects.isNull(dto.getTelephone()) || dto.getTelephone().isBlank()) {
      return "電話番号は必須です";
    }
    if (!dto.getTelephone().matches("^\\d{2,4}-?\\d{2,4}-?\\d{3,4}$")) {
      return "電話番号の形式が不正です（例: 03-1234-5678）";
    }
    return null;
  }

  /**
   * DTOからAddressオブジェクトを作成します.
   *
   * @param dto ユーザー登録DTO
   * @return 住所オブジェクト
   */
  private static Address createAddressFromDto(UserRegistrationDto dto) {
    try {
      return new Address(
          new Address.Zipcode(dto.getZipcode()),
          Address.Prefecture.valueOf(dto.getPrefecture()), // 注意: 文字列をenum値に変換
          new Address.Municipalities(dto.getMunicipalities()),
          new Address.DetailAddress(dto.getAddress())); // DTOではaddressフィールド
    } catch (IllegalArgumentException e) {
      // 住所フィールドに問題があれば例外を再スロー
      throw new IllegalArgumentException("住所情報が不正です: " + e.getMessage(), e);
    }
  }

  /** エラーレスポンス用DTO. */
  private record ErrorResponse(String code, String message) {}
}
