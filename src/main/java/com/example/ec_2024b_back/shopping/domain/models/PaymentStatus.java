package com.example.ec_2024b_back.shopping.domain.models;

/** 支払いの処理状態を表す列挙型 */
public enum PaymentStatus {
  /** 支払いを開始したが、まだ承認されていない状態 */
  PENDING,

  /** 支払いが承認された状態（認証/承認済みだが、実際の決済は完了していない場合もある） */
  AUTHORIZED,

  /** 支払いが完了し、金額が決済された状態 */
  CAPTURED,

  /** 支払いが失敗した状態 */
  FAILED,

  /** 支払いがキャンセルされた状態 */
  CANCELLED,

  /** 支払いが返金された状態 */
  REFUNDED,

  /** 支払いが一部返金された状態 */
  PARTIALLY_REFUNDED;

  /**
   * 次のステータスに遷移可能かどうかを判定します
   *
   * @param nextStatus 遷移先のステータス
   * @return 遷移可能な場合はtrue
   */
  public boolean canTransitionTo(PaymentStatus nextStatus) {
    return switch (this) {
      case PENDING -> nextStatus == AUTHORIZED || nextStatus == FAILED || nextStatus == CANCELLED;
      case AUTHORIZED -> nextStatus == CAPTURED || nextStatus == FAILED || nextStatus == CANCELLED;
      case CAPTURED -> nextStatus == REFUNDED || nextStatus == PARTIALLY_REFUNDED;
      case PARTIALLY_REFUNDED -> nextStatus == REFUNDED;
      // 最終状態からの遷移は不可
      case FAILED, CANCELLED, REFUNDED -> false;
    };
  }
}
