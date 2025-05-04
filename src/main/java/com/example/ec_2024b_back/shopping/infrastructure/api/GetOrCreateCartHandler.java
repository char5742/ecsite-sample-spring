package com.example.ec_2024b_back.shopping.infrastructure.api;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.application.usecase.GetOrCreateCartUsecase;
import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** カート取得/作成を処理するハンドラークラス. */
@Component
@RequiredArgsConstructor
public class GetOrCreateCartHandler {

  private final GetOrCreateCartUsecase getOrCreateCartUsecase;

  public Mono<ServerResponse> getOrCreateCart(ServerRequest request) {
    // パスからアカウントIDを取得
    String accountIdStr = request.pathVariable("accountId");
    var accountId = AccountId.of(accountIdStr);

    return getOrCreateCartUsecase
        .execute(accountId)
        .flatMap(
            cart -> {
              // カート内の商品情報をDTOに変換
              List<CartItemResponse> items =
                  cart.getItems().stream()
                      .map(
                          item ->
                              new CartItemResponse(
                                  item.productId().toString(),
                                  item.productName(),
                                  item.quantity(),
                                  item.unitPrice(),
                                  item.calculateTotal()))
                      .toList();

              // レスポンスを作成
              return ServerResponse.ok()
                  .bodyValue(
                      new CartResponse(
                          cart.getId().toString(),
                          cart.getAccountId().toString(),
                          ImmutableList.copyOf(items),
                          cart.calculateTotal(),
                          LocalDateTime.ofInstant(cart.getCreatedAt(), ZoneId.systemDefault()),
                          LocalDateTime.ofInstant(cart.getUpdatedAt(), ZoneId.systemDefault())));
            })
        .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
  }

  /**
   * カート内商品レスポンスのDTO.
   *
   * @param productId 商品ID
   * @param productName 商品名
   * @param quantity 数量
   * @param unitPrice 単価
   * @param subtotal 小計
   */
  record CartItemResponse(
      String productId,
      String productName,
      int quantity,
      BigDecimal unitPrice,
      BigDecimal subtotal) {}

  /**
   * カートレスポンスのDTO.
   *
   * @param cartId カートID
   * @param accountId アカウントID
   * @param items カート内商品一覧
   * @param total 合計金額
   * @param createdAt 作成日時
   * @param updatedAt 更新日時
   */
  record CartResponse(
      String cartId,
      String accountId,
      ImmutableList<CartItemResponse> items,
      BigDecimal total,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {}
}
