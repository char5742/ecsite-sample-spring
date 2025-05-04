package com.example.ec_2024b_back.shopping.application.usecase;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.application.workflow.GetOrCreateCartWorkflow;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/** カート取得/作成ユースケースを実装するクラス. */
@Service
@RequiredArgsConstructor
public class GetOrCreateCartUsecase {

  private final GetOrCreateCartWorkflow getOrCreateCartWorkflow;
  private final ApplicationEventPublisher event;

  /**
   * カート取得/作成処理を実行し、カートを返すMonoを返します.
   *
   * @param accountId アカウントID
   * @return カートを含むMono
   */
  @Transactional
  public Mono<Cart> execute(AccountId accountId) {
    return getOrCreateCartWorkflow
        .execute(accountId)
        .onErrorMap(
            e ->
                new CartOperationFailedException(
                    "Failed to get or create cart: " + e.getMessage(), e))
        .doOnNext(cart -> cart.getEvents().forEach(event::publishEvent));
  }

  /** カート操作失敗を表すカスタム例外. */
  public static class CartOperationFailedException extends RuntimeException {
    public CartOperationFailedException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
