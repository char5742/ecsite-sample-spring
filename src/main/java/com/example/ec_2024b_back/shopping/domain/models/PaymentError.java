package com.example.ec_2024b_back.shopping.domain.models;

import org.jspecify.annotations.Nullable;

/** 支払いエラー情報を表す値オブジェクト エラーコードとエラーメッセージを保持します */
public record PaymentError(@Nullable String errorCode, @Nullable String errorMessage) {

  /**
   * エラー情報を作成します
   *
   * @param errorCode エラーコード
   * @param errorMessage エラーメッセージ
   * @return 作成されたエラー情報
   */
  public static PaymentError create(@Nullable String errorCode, String errorMessage) {
    if (errorMessage.isBlank()) {
      throw new IllegalArgumentException("エラーメッセージは空白であってはなりません");
    }

    return new PaymentError(errorCode, errorMessage);
  }

  /**
   * エラーがないことを表す空のエラー情報を作成します
   *
   * @return 空のエラー情報
   */
  public static PaymentError empty() {
    return new PaymentError(null, null);
  }

  /**
   * エラー情報があるかどうかを判定します
   *
   * @return エラー情報がある場合はtrue
   */
  public boolean hasError() {
    return errorMessage != null && !errorMessage.isBlank();
  }

  /**
   * データストアからエラー情報を復元します
   *
   * @param errorCode エラーコード
   * @param errorMessage エラーメッセージ
   * @return 復元されたエラー情報
   */
  public static PaymentError reconstruct(
      @Nullable String errorCode, @Nullable String errorMessage) {
    return new PaymentError(errorCode, errorMessage);
  }
}
