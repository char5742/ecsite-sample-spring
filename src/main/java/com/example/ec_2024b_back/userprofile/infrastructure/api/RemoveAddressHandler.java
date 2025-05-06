package com.example.ec_2024b_back.userprofile.infrastructure.api;

import com.example.ec_2024b_back.userprofile.application.usecase.RemoveAddressUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** 住所削除を処理するハンドラークラス. */
@Component
@RequiredArgsConstructor
public class RemoveAddressHandler {

  private final RemoveAddressUsecase removeAddressUsecase;

  public Mono<ServerResponse> removeAddress(ServerRequest request) {
    return request
        .bodyToMono(RemoveAddressRequest.class)
        .flatMap(req -> removeAddressUsecase.execute(req.userProfileId(), req.addressId()))
        .flatMap(
            _ ->
                ServerResponse.ok()
                    .bodyValue(new RemoveAddressResponse("Address removed successfully")))
        .onErrorResume(
            e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage()));
  }

  /**
   * 住所削除リクエストのDTO.
   *
   * @param userProfileId ユーザープロファイルID
   * @param addressId 削除する住所ID
   */
  record RemoveAddressRequest(String userProfileId, String addressId) {}

  /**
   * 住所削除成功時のレスポンスDTO.
   *
   * @param message 成功メッセージ
   */
  record RemoveAddressResponse(String message) {}
}
