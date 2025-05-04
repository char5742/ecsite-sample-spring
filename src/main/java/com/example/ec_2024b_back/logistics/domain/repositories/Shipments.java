package com.example.ec_2024b_back.logistics.domain.repositories;

import com.example.ec_2024b_back.logistics.ShipmentId;
import com.example.ec_2024b_back.logistics.domain.models.Shipment;
import com.example.ec_2024b_back.shopping.domain.models.OrderId;
import org.jmolecules.ddd.types.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** 配送情報のリポジトリインターフェース */
public interface Shipments extends Repository<Shipment, ShipmentId> {

  /**
   * 配送IDで配送情報を検索します
   *
   * @param id 配送ID
   * @return 見つかった配送情報を含むMono、見つからない場合は空のMono
   */
  Mono<Shipment> findById(ShipmentId id);

  /**
   * 注文IDに関連する配送情報を検索します
   *
   * @param orderId 注文ID
   * @return 見つかった配送情報のFlux
   */
  Flux<Shipment> findByOrderId(OrderId orderId);

  /**
   * 配送情報を保存します
   *
   * @param shipment 保存する配送情報
   * @return 保存された配送情報を含むMono
   */
  Mono<Shipment> save(Shipment shipment);
}
