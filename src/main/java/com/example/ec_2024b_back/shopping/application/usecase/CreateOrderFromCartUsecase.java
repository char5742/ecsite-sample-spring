package com.example.ec_2024b_back.shopping.application.usecase;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.application.workflow.CreateOrderFromCartWorkflow;
import com.example.ec_2024b_back.shopping.domain.models.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/** カートから注文作成ユースケースを実装するクラス. */
@Service
@RequiredArgsConstructor
public class CreateOrderFromCartUsecase {

  private final CreateOrderFromCartWorkflow createOrderFromCartWorkflow;
  private final ApplicationEventPublisher event;

  /**
   * カートから注文作成処理を実行し、作成された注文を返すMonoを返します.
   *
   * @param accountId アカウントID
   * @param shippingAddress 配送先住所
   * @param paymentMethod 支払い方法
   * @return 作成された注文を含むMono
   */
  @Transactional
  public Mono<Order> execute(AccountId accountId, String shippingAddress, String paymentMethod) {
    return createOrderFromCartWorkflow
        .execute(accountId, shippingAddress)
        .onErrorMap(
            e -> new OrderCreationFailedException("Failed to create order: " + e.getMessage(), e))
        .doOnNext(order -> order.getEvents().forEach(event::publishEvent));
  }

  /** 注文作成失敗を表すカスタム例外. */
  public static class OrderCreationFailedException extends RuntimeException {
    public OrderCreationFailedException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
