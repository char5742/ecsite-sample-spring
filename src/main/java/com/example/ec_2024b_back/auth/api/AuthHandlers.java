package com.example.ec_2024b_back.auth.api;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** Authentication handlers interface exposed to the root module for routing. */
public interface AuthHandlers {
  /**
   * Login with email handler.
   *
   * @param request the server request
   * @return the server response
   */
  Mono<ServerResponse> login(ServerRequest request);

  /**
   * Signup with email handler.
   *
   * @param request the server request
   * @return the server response
   */
  Mono<ServerResponse> signup(ServerRequest request);
}
