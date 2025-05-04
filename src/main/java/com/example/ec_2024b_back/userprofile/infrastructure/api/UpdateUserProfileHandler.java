package com.example.ec_2024b_back.userprofile.infrastructure.api;

import com.example.ec_2024b_back.userprofile.application.usecase.UpdateUserProfileUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** ユーザープロファイル更新を処理するハンドラークラス. */
@Component
@RequiredArgsConstructor
public class UpdateUserProfileHandler {

  private final UpdateUserProfileUsecase updateUserProfileUsecase;

  public Mono<ServerResponse> updateProfile(ServerRequest request) {
    return request
        .bodyToMono(UpdateProfileRequest.class)
        .flatMap(req -> updateUserProfileUsecase.execute(req.userProfileId(), req.name()))
        .flatMap(
            profile ->
                ServerResponse.ok()
                    .bodyValue(
                        new UpdateProfileResponse(profile.getId().toString(), profile.getName())))
        .onErrorResume(
            e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage()));
  }

  /**
   * プロファイル更新リクエストのDTO.
   *
   * @param userProfileId ユーザープロファイルID
   * @param name 更新後のユーザー名
   */
  record UpdateProfileRequest(String userProfileId, String name) {}

  /**
   * プロファイル更新成功時のレスポンスDTO.
   *
   * @param userProfileId ユーザープロファイルID
   * @param name 更新後のユーザー名
   */
  record UpdateProfileResponse(String userProfileId, String name) {}
}
