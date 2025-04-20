package com.example.ec_2024b_back;

import com.example.ec_2024b_back.account.application.usecase.LoginUsecase;
import com.example.ec_2024b_back.api.DefaultApi;
import com.example.ec_2024b_back.model.AddItemDto;
import com.example.ec_2024b_back.model.DeleteItemDto;
import com.example.ec_2024b_back.model.GetShoppingCartDto;
import com.example.ec_2024b_back.model.LoginDto;
import com.example.ec_2024b_back.model.PagingRequest;
import com.example.ec_2024b_back.model.RequestInfo;
import com.example.ec_2024b_back.model.SearchDto;
import com.example.ec_2024b_back.model.UserInfo;
import com.example.ec_2024b_back.model.UserRegistrationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AppController implements DefaultApi {
  private final LoginUsecase loginUsecase;

  @Override
  public Mono<ResponseEntity<Object>> addItem(
      Mono<AddItemDto> addItemDto, ServerWebExchange exchange) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Mono<ResponseEntity<Object>> delete(
      Mono<DeleteItemDto> deleteItemDto, ServerWebExchange exchange) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Mono<ResponseEntity<Object>> finalized(
      Mono<RequestInfo> requestInfo, ServerWebExchange exchange) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Mono<ResponseEntity<Object>> getAllItem(ServerWebExchange exchange) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Mono<ResponseEntity<Object>> getPage(
      Mono<PagingRequest> pagingRequest, ServerWebExchange exchange) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Mono<ResponseEntity<UserInfo>> getUserInfo(ServerWebExchange exchange) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Mono<ResponseEntity<Flux<byte[]>>> img(String name, ServerWebExchange exchange) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Mono<ResponseEntity<Object>> login(Mono<LoginDto> loginDto, ServerWebExchange exchange) {
    return loginDto
        .flatMap(
            dto -> {
              // リクエストの基本的なバリデーションを実施
              if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
              }
              if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
              }

              return loginUsecase
                  .execute(dto)
                  .map(
                      result -> {
                        // 明示的にJSON Content-Typeを指定
                        return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body((Object) result);
                      });
            })
        .onErrorResume(
            LoginUsecase.AuthenticationFailedException.class,
            e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
  }

  @Override
  public Mono<ResponseEntity<Object>> registerUser(
      Mono<UserRegistrationDto> userRegistrationDto, ServerWebExchange exchange) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Mono<ResponseEntity<Object>> search(
      Mono<SearchDto> searchDto, ServerWebExchange exchange) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Mono<ResponseEntity<String>> showCreatePage(ServerWebExchange exchange) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Mono<ResponseEntity<Object>> showDetailPage(String id, ServerWebExchange exchange) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Mono<ResponseEntity<Object>> showShoppingCart(
      Mono<GetShoppingCartDto> getShoppingCartDto, ServerWebExchange exchange) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
