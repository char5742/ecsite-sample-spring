package com.example.ec_2024b_back.userprofile.api;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** User Profile handlers interface exposed to the root module for routing. */
public interface UserProfileHandlers {
  /**
   * Create user profile handler.
   *
   * @param request the server request
   * @return the server response
   */
  Mono<ServerResponse> createProfile(ServerRequest request);

  /**
   * Update user profile handler.
   *
   * @param request the server request
   * @return the server response
   */
  Mono<ServerResponse> updateProfile(ServerRequest request);

  /**
   * Add address to user profile handler.
   *
   * @param request the server request
   * @return the server response
   */
  Mono<ServerResponse> addAddress(ServerRequest request);

  /**
   * Remove address from user profile handler.
   *
   * @param request the server request
   * @return the server response
   */
  Mono<ServerResponse> removeAddress(ServerRequest request);
}
