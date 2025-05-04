package com.example.ec_2024b_back.shopping.api;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** Shopping handlers interface exposed to the root module for routing. */
public interface ShoppingHandlers {
  /**
   * Get or create cart handler.
   *
   * @param request the server request
   * @return the server response
   */
  Mono<ServerResponse> getOrCreateCart(ServerRequest request);

  /**
   * Add item to cart handler.
   *
   * @param request the server request
   * @return the server response
   */
  Mono<ServerResponse> addItemToCart(ServerRequest request);

  /**
   * Create order from cart handler.
   *
   * @param request the server request
   * @return the server response
   */
  Mono<ServerResponse> createOrder(ServerRequest request);

  /**
   * Initiate payment handler.
   *
   * @param request the server request
   * @return the server response
   */
  Mono<ServerResponse> initiatePayment(ServerRequest request);
}
