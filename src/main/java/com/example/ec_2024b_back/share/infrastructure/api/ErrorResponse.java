package com.example.ec_2024b_back.share.infrastructure.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

/** API共通エラーレスポンスクラス. */
@JsonInclude(Include.NON_NULL)
public record ErrorResponse(
    String status,
    int code,
    String message,
    LocalDateTime timestamp,
    String path,
    @Nullable ImmutableList<ValidationError> errors) {

  /**
   * バリデーションエラー情報.
   *
   * @param field エラーが発生したフィールド名
   * @param message エラーメッセージ
   */
  public record ValidationError(String field, String message) {}

  /**
   * 単一エラーのレスポンスを作成する.
   *
   * @param status HTTPステータスを表す文字列
   * @param code HTTPステータスコード
   * @param message エラーメッセージ
   * @param path リクエストパス
   * @return エラーレスポンス
   */
  public static ErrorResponse of(String status, int code, String message, String path) {
    return new ErrorResponse(
        status, code, message, LocalDateTime.now(ZoneId.systemDefault()), path, null);
  }

  /**
   * バリデーションエラーのレスポンスを作成する.
   *
   * @param status HTTPステータスを表す文字列
   * @param code HTTPステータスコード
   * @param message エラーメッセージ
   * @param path リクエストパス
   * @param validationErrors バリデーションエラーのリスト
   * @return エラーレスポンス
   */
  public static ErrorResponse ofValidationError(
      String status,
      int code,
      String message,
      String path,
      List<ValidationError> validationErrors) {
    return new ErrorResponse(
        status,
        code,
        message,
        LocalDateTime.now(ZoneId.systemDefault()),
        path,
        ImmutableList.copyOf(validationErrors));
  }

  /** ValidationErrorを追加するためのビルダー. */
  public static class Builder {
    private final String status;
    private final int code;
    private final String message;
    private final String path;
    private final List<ValidationError> errors = new ArrayList<>();

    public Builder(String status, int code, String message, String path) {
      this.status = status;
      this.code = code;
      this.message = message;
      this.path = path;
    }

    @CanIgnoreReturnValue
    public Builder addValidationError(String field, String errorMessage) {
      this.errors.add(new ValidationError(field, errorMessage));
      return this;
    }

    public ErrorResponse build() {
      return new ErrorResponse(
          status,
          code,
          message,
          LocalDateTime.now(ZoneId.systemDefault()),
          path,
          ImmutableList.copyOf(errors));
    }
  }
}
