package com.example.ec_2024b_back.logistics.application.workflow;

import com.example.ec_2024b_back.logistics.ShipmentId;
import com.example.ec_2024b_back.logistics.domain.models.Shipment;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import java.time.Instant;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Mono;

/** 配送完了を記録するワークフロー */
public interface MarkShipmentDeliveredWorkflow {

  /** 配送情報を検索するステップ */
  @FunctionalInterface
  interface FindShipmentStep extends Function<Context.Input, Mono<Context.ShipmentFound>> {}

  /** 配送完了を記録するステップ */
  @FunctionalInterface
  interface MarkDeliveredStep
      extends Function<Context.ShipmentFound, Mono<Context.MarkedDelivered>> {}

  /** 注文の配送完了を記録するステップ */
  @FunctionalInterface
  interface UpdateOrderStep extends Function<Context.MarkedDelivered, Mono<Context.OrderUpdated>> {}

  sealed interface Context
      permits Context.Input, Context.ShipmentFound, Context.MarkedDelivered, Context.OrderUpdated {
    record Input(
        ShipmentId shipmentId, @Nullable String receiverName, @Nullable Instant deliveredAt)
        implements Context {}

    record ShipmentFound(Shipment shipment, @Nullable String receiverName, Instant deliveredAt)
        implements Context {}

    record MarkedDelivered(Shipment updatedShipment) implements Context {}

    record OrderUpdated(Shipment shipment) implements Context {}
  }

  /**
   * 配送完了処理を実行します
   *
   * @param shipmentId 配送ID
   * @param receiverName 受取人名（オプション）
   * @param deliveredAt 配送完了時刻（指定がない場合は現在時刻を使用）
   * @return 更新された配送情報を含むMono
   */
  Mono<Shipment> execute(
      ShipmentId shipmentId, @Nullable String receiverName, @Nullable Instant deliveredAt);

  /** 配送情報が見つからない場合のカスタム例外 */
  class ShipmentNotFoundException extends DomainException {
    public ShipmentNotFoundException(ShipmentId shipmentId) {
      super("配送ID: " + shipmentId + " は見つかりません");
    }
  }

  /** 配送完了の記録に失敗した場合のカスタム例外 */
  class ShipmentDeliveryMarkingException extends DomainException {
    public ShipmentDeliveryMarkingException(String message) {
      super(message);
    }
  }
}
