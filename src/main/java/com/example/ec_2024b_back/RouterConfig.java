package com.example.ec_2024b_back;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.example.ec_2024b_back.auth.api.AuthHandlers;
import com.example.ec_2024b_back.sample.infrastructure.api.CreateSampleHandler;
import com.example.ec_2024b_back.shopping.api.ShoppingHandlers;
import com.example.ec_2024b_back.userprofile.api.UserProfileHandlers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/** API ルーティング設定クラス. OpenAPIの仕様に基づいてエンドポイントを定義します。 */
@Configuration
public class RouterConfig {

  /**
   * APIルートの定義. 実装済みのハンドラーとエンドポイントのマッピングを行います。
   *
   * @param authHandlers 認証系ハンドラー
   * @param userProfileHandlers ユーザープロファイル系ハンドラー
   * @param shoppingHandlers ショッピング系ハンドラー
   * @param createSampleHandler サンプル作成ハンドラー
   * @return ルーター機能
   */
  @Bean
  public RouterFunction<ServerResponse> apiRoutes(
      AuthHandlers authHandlers,
      UserProfileHandlers userProfileHandlers,
      ShoppingHandlers shoppingHandlers,
      CreateSampleHandler createSampleHandler) {

    return route()
        // 認証関連エンドポイント
        .POST("/api/authentication/login", accept(MediaType.APPLICATION_JSON), authHandlers::login)

        // サインアップエンドポイント
        .POST(
            "/api/authentication/signup", accept(MediaType.APPLICATION_JSON), authHandlers::signup)

        // ユーザープロファイル関連エンドポイント
        .POST(
            "/api/userprofiles",
            accept(MediaType.APPLICATION_JSON),
            userProfileHandlers::createProfile)
        .PUT(
            "/api/userprofiles",
            accept(MediaType.APPLICATION_JSON),
            userProfileHandlers::updateProfile)
        .POST(
            "/api/userprofiles/addresses",
            accept(MediaType.APPLICATION_JSON),
            userProfileHandlers::addAddress)
        .DELETE(
            "/api/userprofiles/addresses",
            accept(MediaType.APPLICATION_JSON),
            userProfileHandlers::removeAddress)

        // ショッピング関連エンドポイント
        .GET("/api/carts/{accountId}", shoppingHandlers::getOrCreateCart)
        .POST(
            "/api/carts/items", accept(MediaType.APPLICATION_JSON), shoppingHandlers::addItemToCart)
        .POST("/api/orders", accept(MediaType.APPLICATION_JSON), shoppingHandlers::createOrder)
        .POST(
            "/api/payments", accept(MediaType.APPLICATION_JSON), shoppingHandlers::initiatePayment)

        // サンプル関連エンドポイント
        .POST("/api/samples", accept(MediaType.APPLICATION_JSON), createSampleHandler::handle)
        .build();
  }
}
