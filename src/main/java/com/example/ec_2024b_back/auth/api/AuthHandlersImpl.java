package com.example.ec_2024b_back.auth.api;

import com.example.ec_2024b_back.auth.infrastructure.api.LoginWithEmailHandler;
import com.example.ec_2024b_back.auth.infrastructure.api.SignupWithEmailHandler;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Authentication handlers implementation. Delegates actual implementation to specialized handlers.
 */
@Component
@Primary
public class AuthHandlersImpl implements AuthHandlers {

  private final LoginWithEmailHandler loginWithEmailHandler;
  private final SignupWithEmailHandler signupWithEmailHandler;

  /**
   * Constructor.
   *
   * @param loginWithEmailHandler login with email handler
   * @param signupWithEmailHandler signup with email handler
   */
  public AuthHandlersImpl(
      LoginWithEmailHandler loginWithEmailHandler, SignupWithEmailHandler signupWithEmailHandler) {
    this.loginWithEmailHandler = loginWithEmailHandler;
    this.signupWithEmailHandler = signupWithEmailHandler;
  }

  @Override
  public Mono<ServerResponse> login(ServerRequest request) {
    return loginWithEmailHandler.login(request);
  }

  @Override
  public Mono<ServerResponse> signup(ServerRequest request) {
    return signupWithEmailHandler.signup(request);
  }
}
