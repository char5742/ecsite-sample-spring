package com.example.ec_2024b_back.logistics.domain.services;

import com.example.ec_2024b_back.logistics.ShipmentId;
import com.example.ec_2024b_back.logistics.domain.models.Shipment;
import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.share.domain.services.TimeProvider;
import com.example.ec_2024b_back.shopping.OrderId;
import java.time.Instant;
import java.time.ZoneId;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

/** 配送情報を生成するファクトリークラス */
@Component
public class ShipmentFactory {
  private final IdGenerator idGenerator;
  private final TimeProvider timeProvider;

  /**
   * コンストラクタ
   *
   * @param idGenerator ID生成器
   * @param timeProvider 時間提供サービス
   */
  public ShipmentFactory(IdGenerator idGenerator, TimeProvider timeProvider) {
    this.idGenerator = idGenerator;
    this.timeProvider = timeProvider;
  }

  /**
   * 注文情報から新しい配送情報を作成します
   *
   * @param orderId 注文ID
   * @param shippingAddress 配送先住所
   * @param shippingMethod 配送方法
   * @param estimatedDeliveryDate 配送予定日時（オプション）
   * @return 作成された配送情報
   */
  public Shipment create(
      OrderId orderId,
      String shippingAddress,
      String shippingMethod,
      @Nullable Instant estimatedDeliveryDate) {

    var shipmentId = new ShipmentId(idGenerator.newId());
    var now = timeProvider.now().atZone(ZoneId.systemDefault()).toInstant();

    return Shipment.create(
        shipmentId, orderId, shippingAddress, shippingMethod, estimatedDeliveryDate, now);
  }
}
