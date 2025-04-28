package com.example.ec_2024b_back;

import com.example.ec_2024b_back.api.DefaultApi;
import com.example.ec_2024b_back.auth.api.LoginHandler;
import com.example.ec_2024b_back.auth.application.usecase.LoginUsecase;
import com.example.ec_2024b_back.model.AddItemDto;
import com.example.ec_2024b_back.model.DeleteItemDto;
import com.example.ec_2024b_back.model.GetShoppingCartDto;
import com.example.ec_2024b_back.model.LoginDto;
import com.example.ec_2024b_back.model.PagingRequest;
import com.example.ec_2024b_back.model.RequestInfo;
import com.example.ec_2024b_back.model.SearchDto;
import com.example.ec_2024b_back.model.UserInfo;
import com.example.ec_2024b_back.model.UserRegistrationDto;
import com.example.ec_2024b_back.user.interfaces.handler.RegisterUserHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** OpenAPI Generator で生成されたインターフェースを実装するAPIコントローラ. 現時点ではログイン機能のみが実装されており、他の機能は今後のPRで順次実装予定です。 */
@RestController
@RequiredArgsConstructor
public class AppController implements DefaultApi {
  private final LoginHandler loginHandler;
  private final RegisterUserHandler registerUserHandler;

  @Override
  public Mono<ResponseEntity<Object>> addItem(
      Mono<AddItemDto> addItemDto, ServerWebExchange exchange) {
    // TODO: issue #XX - 商品追加機能の実装
    throw new UnsupportedOperationException("ショッピングカートへの商品追加機能は未実装です");
  }

  @Override
  public Mono<ResponseEntity<Object>> delete(
      Mono<DeleteItemDto> deleteItemDto, ServerWebExchange exchange) {
    // TODO: issue #XX - 商品削除機能の実装
    throw new UnsupportedOperationException("ショッピングカートからの商品削除機能は未実装です");
  }

  @Override
  public Mono<ResponseEntity<Object>> finalized(
      Mono<RequestInfo> requestInfo, ServerWebExchange exchange) {
    // TODO: issue #XX - 注文確定機能の実装
    throw new UnsupportedOperationException("注文確定機能は未実装です");
  }

  @Override
  public Mono<ResponseEntity<Object>> getAllItem(ServerWebExchange exchange) {
    // TODO: issue #XX - 商品一覧取得機能の実装
    throw new UnsupportedOperationException("商品一覧取得機能は未実装です");
  }

  @Override
  public Mono<ResponseEntity<Object>> getPage(
      Mono<PagingRequest> pagingRequest, ServerWebExchange exchange) {
    // TODO: issue #XX - ページング機能の実装
    throw new UnsupportedOperationException("ページング機能は未実装です");
  }

  @Override
  public Mono<ResponseEntity<UserInfo>> getUserInfo(ServerWebExchange exchange) {
    // TODO: issue #XX - ユーザー情報取得機能の実装
    throw new UnsupportedOperationException("ユーザー情報取得機能は未実装です");
  }

  @Override
  public Mono<ResponseEntity<Flux<byte[]>>> img(String name, ServerWebExchange exchange) {
    // TODO: issue #XX - 画像取得機能の実装
    throw new UnsupportedOperationException("画像取得機能は未実装です");
  }

  @Override
  public Mono<ResponseEntity<Object>> login(Mono<LoginDto> loginDto, ServerWebExchange exchange) {
    return loginDto
        .flatMap(loginHandler::login)
        .onErrorResume(
            LoginUsecase.AuthenticationFailedException.class,
            e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
  }

  @Override
  public Mono<ResponseEntity<Object>> registerUser(
      Mono<UserRegistrationDto> userRegistrationDto, ServerWebExchange exchange) {
    return userRegistrationDto.flatMap(
        dto -> {
          // RegisterUserHandlerクラスのロジックを利用してユーザー登録処理を実行
          // リクエストのバリデーションや住所オブジェクトの変換を行う
          return registerUserHandler.handleRegistration(dto);
        });
  }

  @Override
  public Mono<ResponseEntity<Object>> search(
      Mono<SearchDto> searchDto, ServerWebExchange exchange) {
    // TODO: issue #XX - 検索機能の実装
    throw new UnsupportedOperationException("検索機能は未実装です");
  }

  @Override
  public Mono<ResponseEntity<String>> showCreatePage(ServerWebExchange exchange) {
    // TODO: issue #XX - 作成ページ表示機能の実装
    throw new UnsupportedOperationException("作成ページ表示機能は未実装です");
  }

  @Override
  public Mono<ResponseEntity<Object>> showDetailPage(String id, ServerWebExchange exchange) {
    // TODO: issue #XX - 詳細ページ表示機能の実装
    throw new UnsupportedOperationException("詳細ページ表示機能は未実装です");
  }

  @Override
  public Mono<ResponseEntity<Object>> showShoppingCart(
      Mono<GetShoppingCartDto> getShoppingCartDto, ServerWebExchange exchange) {
    // TODO: issue #XX - ショッピングカート表示機能の実装
    throw new UnsupportedOperationException("ショッピングカート表示機能は未実装です");
  }
}
