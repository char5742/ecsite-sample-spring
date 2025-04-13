package com.example.ec_2024b_back.share.domain.exceptions;

/**
 * ドメイン例外クラス
 *
 * <p>domain層で発生する例外を表すクラスです。
 */
public class DomainException extends RuntimeException {
  public DomainException(String message) {
    super(message);
  }

  public DomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
