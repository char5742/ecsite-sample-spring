package com.example.ec_2024b_back.userprofile.infrastructure.api;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.userprofile.application.usecase.CreateUserProfileUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** ユーザープロファイル作成を処理するハンドラークラス. */
@Component
@RequiredArgsConstructor
public class CreateUserProfileHandler {

  private final CreateUserProfileUsecase createUserProfileUsecase;

  public Mono<ServerResponse> createProfile(ServerRequest request) {
    return request
        .bodyToMono(CreateProfileRequest.class)
        .flatMap(
            req -> {
              var accountId = AccountId.of(req.accountId());
              return createUserProfileUsecase.execute(accountId, req.name());
            })
        .flatMap(
            profile ->
                ServerResponse.status(HttpStatus.CREATED)
                    .bodyValue(
                        new CreateProfileResponse(profile.getId().toString(), profile.getName())))
        .onErrorResume(
            e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage()));
  }

  /**
   * プロファイル作成リクエストのDTO.
   *
   * @param accountId アカウントID
   * @param name ユーザー名
   */
  record CreateProfileRequest(String accountId, String name) {}

  /**
   * プロファイル作成成功時のレスポンスDTO.
   *
   * @param userProfileId 作成されたユーザープロファイルID
   * @param name ユーザー名
   */
  record CreateProfileResponse(String userProfileId, String name) {}
}
