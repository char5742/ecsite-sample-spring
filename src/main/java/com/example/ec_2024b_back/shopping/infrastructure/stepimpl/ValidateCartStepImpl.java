package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.shopping.application.workflow.CreateOrderFromCartWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.CreateOrderFromCartWorkflow.ValidateCartStep;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** カートの内容を検証するステップの実装 */
@Component
@RequiredArgsConstructor
public class ValidateCartStepImpl implements ValidateCartStep {

  // 税率と配送料（今回は簡易的に固定値）
  private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10%
  private static final BigDecimal SHIPPING_COST = new BigDecimal("500"); // 500円

  @Override
  public Mono<CreateOrderFromCartWorkflow.Context.Validated> apply(
      CreateOrderFromCartWorkflow.Context.CartFound context) {
    var cart = context.cart();

    // カートが空でないことを確認
    if (cart.getItems().isEmpty()) {
      return Mono.error(new CreateOrderFromCartWorkflow.EmptyCartException("空のカートから注文を作成できません"));
    }

    // 配送先住所が有効であることを確認
    if (context.shippingAddress() == null || context.shippingAddress().isBlank()) {
      return Mono.error(
          new CreateOrderFromCartWorkflow.InvalidShippingAddressException("配送先住所は必須です"));
    }

    // バリデーション成功
    return Mono.just(
        new CreateOrderFromCartWorkflow.Context.Validated(
            cart, context.shippingAddress(), SHIPPING_COST, TAX_RATE));
  }
}
