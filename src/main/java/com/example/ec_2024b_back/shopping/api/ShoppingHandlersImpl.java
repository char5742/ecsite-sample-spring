package com.example.ec_2024b_back.shopping.api;

import com.example.ec_2024b_back.shopping.infrastructure.api.AddItemToCartHandler;
import com.example.ec_2024b_back.shopping.infrastructure.api.CreateOrderFromCartHandler;
import com.example.ec_2024b_back.shopping.infrastructure.api.GetOrCreateCartHandler;
import com.example.ec_2024b_back.shopping.infrastructure.api.InitiatePaymentHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** Shopping handlers implementation that delegates to the actual handlers. */
@Component
@RequiredArgsConstructor
public class ShoppingHandlersImpl implements ShoppingHandlers {

  private final GetOrCreateCartHandler getOrCreateCartHandler;
  private final AddItemToCartHandler addItemToCartHandler;
  private final CreateOrderFromCartHandler createOrderFromCartHandler;
  private final InitiatePaymentHandler initiatePaymentHandler;

  @Override
  public Mono<ServerResponse> getOrCreateCart(ServerRequest request) {
    return getOrCreateCartHandler.getOrCreateCart(request);
  }

  @Override
  public Mono<ServerResponse> addItemToCart(ServerRequest request) {
    return addItemToCartHandler.addItemToCart(request);
  }

  @Override
  public Mono<ServerResponse> createOrder(ServerRequest request) {
    return createOrderFromCartHandler.createOrder(request);
  }

  @Override
  public Mono<ServerResponse> initiatePayment(ServerRequest request) {
    return initiatePaymentHandler.initiatePayment(request);
  }
}
