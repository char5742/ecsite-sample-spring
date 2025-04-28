package com.example.ec_2024b_back.user.interfaces.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import com.example.ec_2024b_back.user.interfaces.handler.RegisterUserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/** ユーザー登録関連のルーティング設定. */
@Configuration
public class UserRouter {

  /**
   * ユーザー登録用のルーティング設定.
   *
   * @param registerUserHandler ユーザー登録処理を行うハンドラー
   * @return ルーティング設定
   */
  @Bean
  public RouterFunction<ServerResponse> userRoutes(RegisterUserHandler registerUserHandler) {
    return RouterFunctions.route(
        POST("/api/registration").and(accept(MediaType.APPLICATION_JSON)),
        registerUserHandler::registerUser);
  }
}
