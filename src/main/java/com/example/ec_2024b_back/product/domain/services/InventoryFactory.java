package com.example.ec_2024b_back.product.domain.services;

import com.example.ec_2024b_back.product.InventoryId;
import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.product.domain.models.Inventory;
import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 在庫を作成するファクトリー */
@Component
@RequiredArgsConstructor
public class InventoryFactory {
  private final IdGenerator idGen;

  /**
   * 新しい在庫を作成します
   *
   * @param productId 商品ID
   * @param availableQuantity 利用可能数量
   * @return 作成された在庫
   */
  public Inventory create(ProductId productId, int availableQuantity) {
    return Inventory.create(new InventoryId(idGen.newId()), productId, availableQuantity);
  }

  /**
   * 新しい在庫を作成します (初期値0)
   *
   * @param productId 商品ID
   * @return 作成された在庫
   */
  public Inventory createEmpty(ProductId productId) {
    return create(productId, 0);
  }
}
