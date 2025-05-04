package com.example.ec_2024b_back.shopping.application.usecase;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.shopping.application.workflow.AddItemToCartWorkflow;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/** カートに商品追加ユースケースを実装するクラス. */
@Service
@RequiredArgsConstructor
public class AddItemToCartUsecase {

  private final AddItemToCartWorkflow addItemToCartWorkflow;
  private final ApplicationEventPublisher event;

  /**
   * カートに商品追加処理を実行し、更新されたカートを返すMonoを返します.
   *
   * @param accountId アカウントID
   * @param productId 追加する商品ID
   * @param productName 追加する商品名
   * @param unitPrice 単価
   * @param quantity 数量
   * @return 更新されたカートを含むMono
   */
  @Transactional
  public Mono<Cart> execute(
      AccountId accountId,
      ProductId productId,
      String productName,
      int quantity,
      BigDecimal unitPrice) {
    return addItemToCartWorkflow
        .execute(accountId, productId, productName, unitPrice, quantity)
        .onErrorMap(
            e ->
                new CartOperationFailedException(
                    "Failed to add item to cart: " + e.getMessage(), e))
        .doOnNext(cart -> cart.getEvents().forEach(event::publishEvent));
  }

  /** カート操作失敗を表すカスタム例外. */
  public static class CartOperationFailedException extends RuntimeException {
    public CartOperationFailedException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
