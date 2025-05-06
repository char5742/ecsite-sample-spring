package com.example.ec_2024b_back.shopping.infrastructure.workflowimpl;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.application.workflow.CreateOrderFromCartWorkflow;
import com.example.ec_2024b_back.shopping.domain.models.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** カートから注文を作成するワークフローの実装 */
@Component
@RequiredArgsConstructor
public class CreateOrderFromCartWorkflowImpl implements CreateOrderFromCartWorkflow {

  private final GetCartStep getCartStep;
  private final ValidateCartStep validateCartStep;
  private final CreateOrderStep createOrderStep;
  private final ClearCartStep clearCartStep;
  private final SaveOrderStep saveOrderStep;

  @Override
  public Mono<Order> execute(AccountId accountId, String shippingAddress) {
    var input = new Context.Input(accountId, shippingAddress);

    return getCartStep
        .apply(input)
        .flatMap(validateCartStep)
        .flatMap(createOrderStep)
        .flatMap(clearCartStep)
        .flatMap(saveOrderStep)
        .map(Context.Complete::order);
  }
}
