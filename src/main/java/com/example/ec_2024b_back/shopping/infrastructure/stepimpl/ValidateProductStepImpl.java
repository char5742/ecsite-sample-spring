package com.example.ec_2024b_back.shopping.infrastructure.stepimpl;

import com.example.ec_2024b_back.shopping.application.workflow.AddItemToCartWorkflow;
import com.example.ec_2024b_back.shopping.application.workflow.AddItemToCartWorkflow.ValidateProductStep;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 商品情報を検証するステップの実装 */
@Component
@RequiredArgsConstructor
public class ValidateProductStepImpl implements ValidateProductStep {

  @Override
  public Mono<AddItemToCartWorkflow.Context.Validated> apply(
      AddItemToCartWorkflow.Context.CartFound context) {
    // 商品名のバリデーション
    if (context.productName() == null || context.productName().isBlank()) {
      return Mono.error(new AddItemToCartWorkflow.InvalidProductInfoException("商品名は必須です"));
    }

    // 単価のバリデーション
    if (context.unitPrice() == null || context.unitPrice().compareTo(BigDecimal.ZERO) < 0) {
      return Mono.error(new AddItemToCartWorkflow.InvalidProductInfoException("単価は0以上の値が必要です"));
    }

    // 数量のバリデーション
    if (context.quantity() <= 0) {
      return Mono.error(new AddItemToCartWorkflow.InvalidProductInfoException("数量は1以上の値が必要です"));
    }

    // バリデーション成功
    return Mono.just(
        new AddItemToCartWorkflow.Context.Validated(
            context.cart(),
            context.productId(),
            context.productName(),
            context.unitPrice(),
            context.quantity()));
  }
}
