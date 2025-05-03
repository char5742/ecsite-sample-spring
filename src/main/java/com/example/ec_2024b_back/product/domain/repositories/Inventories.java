package com.example.ec_2024b_back.product.domain.repositories;

import com.example.ec_2024b_back.product.InventoryId;
import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.product.domain.models.Inventory;
import reactor.core.publisher.Mono;

/** 在庫リポジトリインターフェース */
public interface Inventories {

  /**
   * IDによって在庫を検索します
   *
   * @param id 在庫ID
   * @return 在庫を含むMono、見つからない場合は empty
   */
  Mono<Inventory> findById(InventoryId id);

  /**
   * 商品IDによって在庫を検索します
   *
   * @param productId 商品ID
   * @return 在庫を含むMono、見つからない場合は empty
   */
  Mono<Inventory> findByProductId(ProductId productId);

  /**
   * 在庫を保存します
   *
   * @param inventory 保存する在庫
   * @return 保存された在庫を含むMono
   */
  Mono<Inventory> save(Inventory inventory);
}
