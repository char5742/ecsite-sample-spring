package com.example.ec_2024b_back.logistics.application.workflow;

import com.example.ec_2024b_back.logistics.ShipmentId;
import com.example.ec_2024b_back.logistics.domain.models.Shipment;
import com.example.ec_2024b_back.logistics.domain.models.ShipmentStatus;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Mono;

/** 配送状態を更新するワークフロー */
public interface UpdateShipmentStatusWorkflow {

  /** 配送情報を検索するステップ */
  @FunctionalInterface
  interface FindShipmentStep extends Function<Context.Input, Mono<Context.ShipmentFound>> {}

  /** 配送状態を更新するステップ */
  @FunctionalInterface
  interface UpdateStatusStep extends Function<Context.ShipmentFound, Mono<Context.StatusUpdated>> {}

  /** 注文の配送状態を更新するステップ（オプション） */
  @FunctionalInterface
  interface UpdateOrderShipmentStatusStep
      extends Function<Context.StatusUpdated, Mono<Context.OrderUpdated>> {}

  sealed interface Context
      permits Context.Input, Context.ShipmentFound, Context.StatusUpdated, Context.OrderUpdated {
    record Input(
        ShipmentId shipmentId,
        ShipmentStatus newStatus,
        @Nullable String trackingNumber,
        @Nullable String note,
        boolean updateOrder)
        implements Context {}

    record ShipmentFound(
        Shipment shipment,
        ShipmentStatus newStatus,
        @Nullable String trackingNumber,
        @Nullable String note,
        boolean updateOrder)
        implements Context {}

    record StatusUpdated(Shipment updatedShipment, boolean updateOrder) implements Context {}

    record OrderUpdated(Shipment shipment) implements Context {}
  }

  /**
   * 配送状態更新処理を実行します
   *
   * @param shipmentId 配送ID
   * @param newStatus 新しい配送状態
   * @param trackingNumber 追跡番号（オプション）
   * @param note 備考（オプション）
   * @param updateOrder 注文の配送状態も更新するかどうか
   * @return 更新された配送情報を含むMono
   */
  Mono<Shipment> execute(
      ShipmentId shipmentId,
      ShipmentStatus newStatus,
      @Nullable String trackingNumber,
      @Nullable String note,
      boolean updateOrder);

  /** 配送情報が見つからない場合のカスタム例外 */
  class ShipmentNotFoundException extends DomainException {
    public ShipmentNotFoundException(ShipmentId shipmentId) {
      super("配送ID: " + shipmentId + " は見つかりません");
    }
  }

  /** 配送状態の更新に失敗した場合のカスタム例外 */
  class ShipmentStatusUpdateException extends DomainException {
    public ShipmentStatusUpdateException(String message) {
      super(message);
    }
  }
}
