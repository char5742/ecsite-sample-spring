package com.example.ec_2024b_back.share.infrastructure.api;

import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.share.infrastructure.api.ErrorResponse.ValidationError;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

/** 全APIのグローバルエラーハンドラー. 発生した例外を適切なHTTPステータスとレスポンス形式に変換します。 */
@Component
@Order(-2) // DefaultErrorWebExceptionHandlerより高い優先度で実行
public class GlobalErrorHandler extends AbstractErrorWebExceptionHandler {

  /**
   * コンストラクター.
   *
   * @param errorAttributes エラー属性
   * @param resources Webプロパティ
   * @param applicationContext アプリケーションコンテキスト
   * @param configurer サーバーコーデック設定
   */
  public GlobalErrorHandler(
      ErrorAttributes errorAttributes,
      WebProperties.Resources resources,
      ApplicationContext applicationContext,
      ServerCodecConfigurer configurer) {
    super(errorAttributes, resources, applicationContext);
    this.setMessageWriters(configurer.getWriters());
    this.setMessageReaders(configurer.getReaders());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
  }

  private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
    Throwable error = getError(request);
    return switch (error) {
      case DomainException e -> handleDomainException(e, request);
      case ServerWebInputException e -> handleServerWebInputException(e, request);
      case ResponseStatusException e -> handleResponseStatusException(e, request);
      default -> handleGenericException(request);
    };
  }

  /** ドメイン例外のハンドリング. ドメイン例外はBadRequestとして扱います。 */
  private static Mono<ServerResponse> handleDomainException(
      DomainException ex, ServerRequest request) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String message = ex.getMessage() != null ? ex.getMessage() : "Bad Request";
    var response =
        ErrorResponse.of(status.getReasonPhrase(), status.value(), message, request.path());

    return ServerResponse.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(response));
  }

  /** ResponseStatusExceptionのハンドリング. 例外が持つHTTPステータスを使用します。 */
  private static Mono<ServerResponse> handleResponseStatusException(
      ResponseStatusException ex, ServerRequest request) {
    HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
    var response =
        ErrorResponse.of(status.getReasonPhrase(), status.value(), ex.getReason(), request.path());

    return ServerResponse.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(response));
  }

  /** ServerWebInputExceptionのハンドリング. バリデーションエラーを含む入力エラーを処理します。 */
  private static Mono<ServerResponse> handleServerWebInputException(
      ServerWebInputException ex, ServerRequest request) {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    // バリデーションエラーの場合
    if (ex.getCause() instanceof BindingResult bindingResult) {
      List<ValidationError> validationErrors =
          bindingResult.getFieldErrors().stream()
              .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
              .collect(Collectors.toList());

      var response =
          ErrorResponse.ofValidationError(
              status.getReasonPhrase(),
              status.value(),
              "Validation failed",
              request.path(),
              validationErrors);

      return ServerResponse.status(status)
          .contentType(MediaType.APPLICATION_JSON)
          .body(BodyInserters.fromValue(response));
    }

    // その他の入力エラー
    var response =
        ErrorResponse.of(status.getReasonPhrase(), status.value(), ex.getReason(), request.path());

    return ServerResponse.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(response));
  }

  /** 汎用的な例外ハンドリング. 想定外の例外はInternal Server Errorとして扱います。 */
  private static Mono<ServerResponse> handleGenericException(ServerRequest request) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    var response =
        ErrorResponse.of(
            status.getReasonPhrase(),
            status.value(),
            "An unexpected error occurred",
            request.path());

    return ServerResponse.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(response));
  }
}
