package com.example.ec_2024b_back.shopping.infrastructure.workflowimpl;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.shopping.application.workflow.AddItemToCartWorkflow;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** カートに商品を追加するワークフローの実装 */
@Component
@RequiredArgsConstructor
public class AddItemToCartWorkflowImpl implements AddItemToCartWorkflow {

  private final GetCartStep getCartStep;
  private final ValidateProductStep validateProductStep;
  private final AddItemStep addItemStep;
  private final SaveCartStep saveCartStep;

  @Override
  public Mono<Cart> execute(
      AccountId accountId,
      ProductId productId,
      String productName,
      BigDecimal unitPrice,
      int quantity) {

    var input = new Context.Input(accountId, productId, productName, unitPrice, quantity);

    return getCartStep
        .apply(input)
        .flatMap(validateProductStep)
        .flatMap(addItemStep)
        .flatMap(saveCartStep)
        .map(Context.Complete::cart);
  }
}
