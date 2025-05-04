package com.example.ec_2024b_back.shopping.infrastructure.api;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.OrderId;
import com.example.ec_2024b_back.shopping.application.usecase.InitiatePaymentUsecase;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** 支払い開始を処理するハンドラークラス. */
@Component
@RequiredArgsConstructor
public class InitiatePaymentHandler {

  private final InitiatePaymentUsecase initiatePaymentUsecase;

  public Mono<ServerResponse> initiatePayment(ServerRequest request) {
    return request
        .bodyToMono(InitiatePaymentRequest.class)
        .flatMap(
            req ->
                initiatePaymentUsecase.execute(
                    OrderId.of(req.orderId()), AccountId.of(req.accountId()), req.paymentMethod()))
        .flatMap(
            payment ->
                ServerResponse.status(HttpStatus.CREATED)
                    .bodyValue(
                        new InitiatePaymentResponse(
                            payment.getId().toString(),
                            payment.getOrderId().toString(),
                            payment.getAmount(),
                            payment.getStatus().toString(),
                            payment.getPaymentMethod(),
                            LocalDateTime.ofInstant(payment.getCreatedAt(), ZoneId.systemDefault()),
                            LocalDateTime.ofInstant(
                                payment.getUpdatedAt(), ZoneId.systemDefault()))))
        .onErrorResume(
            e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage()));
  }

  /**
   * 支払い開始リクエストのDTO.
   *
   * @param orderId 注文ID
   * @param accountId アカウントID
   * @param paymentMethod 支払い方法
   */
  record InitiatePaymentRequest(String orderId, String accountId, String paymentMethod) {}

  /**
   * 支払い開始成功時のレスポンスDTO.
   *
   * @param paymentId 支払いID
   * @param orderId 注文ID
   * @param amount 金額
   * @param status 支払い状態
   * @param paymentMethod 支払い方法
   * @param createdAt 作成日時
   * @param updatedAt 更新日時
   */
  record InitiatePaymentResponse(
      String paymentId,
      String orderId,
      BigDecimal amount,
      String status,
      String paymentMethod,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {}
}
