package com.example.ec_2024b_back.shopping.infrastructure.api;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.application.usecase.CreateOrderFromCartUsecase;
import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** カートから注文作成を処理するハンドラークラス. */
@Component
@RequiredArgsConstructor
public class CreateOrderFromCartHandler {

  private final CreateOrderFromCartUsecase createOrderFromCartUsecase;

  public Mono<ServerResponse> createOrder(ServerRequest request) {
    return request
        .bodyToMono(CreateOrderRequest.class)
        .flatMap(
            req ->
                createOrderFromCartUsecase.execute(
                    AccountId.of(req.accountId()), req.shippingAddress(), req.paymentMethod()))
        .flatMap(
            order -> {
              // 注文内の商品情報をDTOに変換
              List<OrderItemResponse> items =
                  order.getItems().stream()
                      .map(
                          item ->
                              new OrderItemResponse(
                                  item.productId().toString(),
                                  item.productName(),
                                  item.quantity(),
                                  item.unitPrice(),
                                  item.subtotal()))
                      .toList();

              // レスポンスを作成
              return ServerResponse.status(HttpStatus.CREATED)
                  .bodyValue(
                      new CreateOrderResponse(
                          order.getId().toString(),
                          order.getAccountId().toString(),
                          ImmutableList.copyOf(items),
                          order.getSubtotal(),
                          order.getTax(),
                          order.getShippingCost(),
                          order.getTotalAmount(),
                          order.getStatus().toString(),
                          order.getShippingAddress(),
                          LocalDateTime.ofInstant(order.getCreatedAt(), ZoneId.systemDefault())));
            })
        .onErrorResume(
            e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage()));
  }

  /**
   * 注文作成リクエストのDTO.
   *
   * @param accountId アカウントID
   * @param shippingAddress 配送先住所
   * @param paymentMethod 支払い方法
   */
  record CreateOrderRequest(String accountId, String shippingAddress, String paymentMethod) {}

  /**
   * 注文内商品レスポンスのDTO.
   *
   * @param productId 商品ID
   * @param productName 商品名
   * @param quantity 数量
   * @param unitPrice 単価
   * @param subtotal 小計
   */
  record OrderItemResponse(
      String productId,
      String productName,
      int quantity,
      BigDecimal unitPrice,
      BigDecimal subtotal) {}

  /**
   * 注文作成成功時のレスポンスDTO.
   *
   * @param orderId 注文ID
   * @param accountId アカウントID
   * @param items 注文内商品一覧
   * @param subtotal 小計
   * @param tax 税額
   * @param shippingCost 配送料
   * @param totalAmount 合計金額
   * @param status 注文状態
   * @param shippingAddress 配送先住所
   * @param createdAt 作成日時
   */
  record CreateOrderResponse(
      String orderId,
      String accountId,
      ImmutableList<OrderItemResponse> items,
      BigDecimal subtotal,
      BigDecimal tax,
      BigDecimal shippingCost,
      BigDecimal totalAmount,
      String status,
      String shippingAddress,
      LocalDateTime createdAt) {}
}
