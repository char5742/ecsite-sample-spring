package com.example.ec_2024b_back.shopping.infrastructure.api;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.shopping.application.usecase.AddItemToCartUsecase;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** カートに商品追加を処理するハンドラークラス. */
@Component
@RequiredArgsConstructor
public class AddItemToCartHandler {

  private final AddItemToCartUsecase addItemToCartUsecase;

  public Mono<ServerResponse> addItemToCart(ServerRequest request) {
    return request
        .bodyToMono(AddItemToCartRequest.class)
        .flatMap(
            req ->
                addItemToCartUsecase.execute(
                    AccountId.of(req.accountId()),
                    ProductId.of(req.productId()),
                    req.productName(),
                    req.quantity(),
                    req.unitPrice()))
        .flatMap(
            cart ->
                ServerResponse.ok()
                    .bodyValue(
                        new AddItemToCartResponse(
                            cart.getId().toString(),
                            "Item added to cart successfully",
                            cart.calculateTotal())))
        .onErrorResume(
            e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage()));
  }

  /**
   * カートに商品追加リクエストのDTO.
   *
   * @param accountId アカウントID
   * @param productId 追加する商品ID
   * @param productName 追加する商品名
   * @param quantity 数量
   * @param unitPrice 単価
   */
  record AddItemToCartRequest(
      String accountId, String productId, String productName, int quantity, BigDecimal unitPrice) {}

  /**
   * 商品追加成功時のレスポンスDTO.
   *
   * @param cartId カートID
   * @param message 成功メッセージ
   * @param total 更新後の合計金額
   */
  record AddItemToCartResponse(String cartId, String message, BigDecimal total) {}
}
