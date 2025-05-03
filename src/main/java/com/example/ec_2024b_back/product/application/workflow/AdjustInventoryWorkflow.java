package com.example.ec_2024b_back.product.application.workflow;

import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.product.domain.models.Inventory;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** 在庫調整ワークフロー */
public interface AdjustInventoryWorkflow {

  /** 商品の在庫を検索する */
  @FunctionalInterface
  interface FindInventoryStep extends Function<Context.Input, Mono<Context.InventoryFound>> {}

  /** 在庫を調整する */
  @FunctionalInterface
  interface AdjustQuantityStep extends Function<Context.InventoryFound, Mono<Context.Adjusted>> {}

  sealed interface Context permits Context.Input, Context.InventoryFound, Context.Adjusted {

    record Input(ProductId productId, int quantityDelta) implements Context {}

    record InventoryFound(Inventory inventory, int quantityDelta) implements Context {}

    record Adjusted(Inventory adjustedInventory) implements Context {}
  }

  /**
   * 在庫調整処理を実行します
   *
   * @param productId 商品ID
   * @param quantityDelta 変更量（正の値は入荷、負の値は出荷）
   * @return 調整後の在庫を含むMono
   */
  Mono<Context.Adjusted> execute(ProductId productId, int quantityDelta);

  /** 商品の在庫が見つからない場合のカスタム例外 */
  class InventoryNotFoundException extends DomainException {
    public InventoryNotFoundException(ProductId productId) {
      super("商品ID: " + productId.value() + " の在庫が見つかりません");
    }
  }

  /** 在庫不足のカスタム例外 */
  class InsufficientStockException extends DomainException {
    public InsufficientStockException(ProductId productId, int available, int requested) {
      super(
          "商品ID: " + productId.value() + " の在庫が不足しています。利用可能: " + available + ", 要求: " + requested);
    }
  }
}
