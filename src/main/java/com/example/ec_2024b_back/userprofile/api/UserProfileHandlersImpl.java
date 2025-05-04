package com.example.ec_2024b_back.userprofile.api;

import com.example.ec_2024b_back.userprofile.infrastructure.api.AddAddressHandler;
import com.example.ec_2024b_back.userprofile.infrastructure.api.CreateUserProfileHandler;
import com.example.ec_2024b_back.userprofile.infrastructure.api.RemoveAddressHandler;
import com.example.ec_2024b_back.userprofile.infrastructure.api.UpdateUserProfileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** User Profile handlers implementation that delegates to the actual handlers. */
@Component
@RequiredArgsConstructor
public class UserProfileHandlersImpl implements UserProfileHandlers {

  private final CreateUserProfileHandler createUserProfileHandler;
  private final UpdateUserProfileHandler updateUserProfileHandler;
  private final AddAddressHandler addAddressHandler;
  private final RemoveAddressHandler removeAddressHandler;

  @Override
  public Mono<ServerResponse> createProfile(ServerRequest request) {
    return createUserProfileHandler.createProfile(request);
  }

  @Override
  public Mono<ServerResponse> updateProfile(ServerRequest request) {
    return updateUserProfileHandler.updateProfile(request);
  }

  @Override
  public Mono<ServerResponse> addAddress(ServerRequest request) {
    return addAddressHandler.addAddress(request);
  }

  @Override
  public Mono<ServerResponse> removeAddress(ServerRequest request) {
    return removeAddressHandler.removeAddress(request);
  }
}
