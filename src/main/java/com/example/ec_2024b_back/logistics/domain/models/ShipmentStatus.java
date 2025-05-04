package com.example.ec_2024b_back.logistics.domain.models;

/** 配送の処理状態を表す列挙型 */
public enum ShipmentStatus {
  /** 新規作成された配送 */
  CREATED,

  /** 出荷処理を待機中 */
  PENDING,

  /** 出荷処理済み・配送中 */
  SHIPPED,

  /** 配送先に到着 */
  ARRIVED,

  /** 配送完了 */
  DELIVERED,

  /** 受取人不在などで保留中 */
  ON_HOLD,

  /** キャンセル/返送 */
  RETURNED;

  /**
   * 次のステータスに遷移可能かどうかを判定します
   *
   * @param nextStatus 遷移先のステータス
   * @return 遷移可能な場合はtrue
   */
  public boolean canTransitionTo(ShipmentStatus nextStatus) {
    return switch (this) {
      case CREATED -> nextStatus == PENDING || nextStatus == RETURNED;
      case PENDING -> nextStatus == SHIPPED || nextStatus == RETURNED;
      case SHIPPED -> nextStatus == ARRIVED || nextStatus == ON_HOLD || nextStatus == RETURNED;
      case ARRIVED -> nextStatus == DELIVERED || nextStatus == ON_HOLD || nextStatus == RETURNED;
      case ON_HOLD ->
          nextStatus == SHIPPED
              || nextStatus == ARRIVED
              || nextStatus == DELIVERED
              || nextStatus == RETURNED;
      case DELIVERED, RETURNED -> false; // 最終状態からの遷移は不可
    };
  }
}
