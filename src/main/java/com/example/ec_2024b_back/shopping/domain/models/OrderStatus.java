package com.example.ec_2024b_back.shopping.domain.models;

/** 注文の処理状態を表す列挙型 */
public enum OrderStatus {
  /** 新規作成された注文 */
  CREATED,

  /** 支払いが確認された注文 */
  PAID,

  /** 出荷処理された注文 */
  SHIPPED,

  /** 配送完了した注文 */
  DELIVERED,

  /** キャンセルされた注文 */
  CANCELLED,

  /** 完全に処理が完了した注文 */
  COMPLETED;

  /**
   * 次のステータスに遷移可能かどうかを判定します
   *
   * @param nextStatus 遷移先のステータス
   * @return 遷移可能な場合はtrue
   */
  public boolean canTransitionTo(OrderStatus nextStatus) {
    return switch (this) {
      case CREATED -> nextStatus == PAID || nextStatus == CANCELLED;
      case PAID -> nextStatus == SHIPPED || nextStatus == CANCELLED;
      case SHIPPED -> nextStatus == DELIVERED || nextStatus == CANCELLED;
      case DELIVERED -> nextStatus == COMPLETED;
      case CANCELLED, COMPLETED -> false; // 最終状態からの遷移は不可
    };
  }
}
