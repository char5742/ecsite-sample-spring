package com.example.ec_2024b_back.userprofile.infrastructure.api;

import com.example.ec_2024b_back.userprofile.application.usecase.AddAddressUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** 住所追加を処理するハンドラークラス. */
@Component
@RequiredArgsConstructor
public class AddAddressHandler {

  private final AddAddressUsecase addAddressUsecase;

  public Mono<ServerResponse> addAddress(ServerRequest request) {
    return request
        .bodyToMono(AddAddressRequest.class)
        .flatMap(
            req ->
                addAddressUsecase.execute(
                    req.userProfileId(),
                    req.name(),
                    req.postalCode(),
                    req.prefecture(),
                    req.city(),
                    req.street(),
                    req.building(),
                    req.phoneNumber(),
                    req.isDefault()))
        .flatMap(
            _ ->
                ServerResponse.status(HttpStatus.CREATED)
                    .bodyValue(new AddAddressResponse("Address added successfully")))
        .onErrorResume(
            e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage()));
  }

  /**
   * 住所追加リクエストのDTO.
   *
   * @param userProfileId ユーザープロファイルID
   * @param name 住所名称
   * @param postalCode 郵便番号
   * @param prefecture 都道府県
   * @param city 市区町村
   * @param street 町名・番地
   * @param building 建物名・部屋番号（任意）
   * @param phoneNumber 電話番号
   * @param isDefault デフォルト住所かどうか
   */
  record AddAddressRequest(
      String userProfileId,
      String name,
      String postalCode,
      String prefecture,
      String city,
      String street,
      String building,
      String phoneNumber,
      boolean isDefault) {}

  /**
   * 住所追加成功時のレスポンスDTO.
   *
   * @param message 成功メッセージ
   */
  record AddAddressResponse(String message) {}
}
