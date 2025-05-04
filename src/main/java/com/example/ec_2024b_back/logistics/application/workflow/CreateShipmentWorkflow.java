package com.example.ec_2024b_back.logistics.application.workflow;

import com.example.ec_2024b_back.logistics.domain.models.Shipment;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.shopping.domain.models.Order;
import com.example.ec_2024b_back.shopping.domain.models.OrderId;
import java.time.Instant;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Mono;

/** 配送情報を作成するワークフロー */
public interface CreateShipmentWorkflow {

  /** 注文を検索するステップ */
  @FunctionalInterface
  interface FindOrderStep extends Function<Context.Input, Mono<Context.OrderFound>> {}

  /** 配送情報を作成するステップ */
  @FunctionalInterface
  interface CreateShipmentStep
      extends Function<Context.OrderFound, Mono<Context.ShipmentCreated>> {}

  sealed interface Context permits Context.Input, Context.OrderFound, Context.ShipmentCreated {
    record Input(
        OrderId orderId, @Nullable String shippingMethod, @Nullable Instant estimatedDeliveryDate)
        implements Context {}

    record OrderFound(Order order, String shippingMethod, @Nullable Instant estimatedDeliveryDate)
        implements Context {}

    record ShipmentCreated(Shipment shipment) implements Context {}
  }

  /**
   * 配送情報作成処理を実行します
   *
   * @param orderId 注文ID
   * @param shippingMethod 配送方法（オプション、指定がない場合はデフォルト方法を使用）
   * @param estimatedDeliveryDate 配送予定日時（オプション）
   * @return 作成された配送情報を含むMono
   */
  Mono<Shipment> execute(
      OrderId orderId, @Nullable String shippingMethod, @Nullable Instant estimatedDeliveryDate);

  /** 配送情報作成失敗時のカスタム例外 */
  class ShipmentCreationException extends DomainException {
    public ShipmentCreationException(String message) {
      super(message);
    }
  }

  /** 注文が見つからない場合のカスタム例外 */
  class OrderNotFoundException extends DomainException {
    public OrderNotFoundException(OrderId orderId) {
      super("注文ID: " + orderId + " は見つかりません");
    }
  }

  /** 注文が配送に適切な状態ではない場合のカスタム例外 */
  class OrderNotEligibleForShipmentException extends DomainException {
    public OrderNotEligibleForShipmentException(OrderId orderId, String status) {
      super("注文ID: " + orderId + " は配送に適切な状態ではありません。現在の状態: " + status);
    }
  }
}
