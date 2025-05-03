package com.example.ec_2024b_back.product.domain.models;

import com.example.ec_2024b_back.product.InventoryId;
import com.example.ec_2024b_back.product.ProductId;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Var;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.event.types.DomainEvent;

/** 在庫集約 特定の商品に対する在庫数量と予約状況を管理します */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Inventory implements AggregateRoot<Inventory, InventoryId> {
  private final InventoryId id;
  private final ProductId productId;
  private final int availableQuantity; // 利用可能な在庫数
  private final int reservedQuantity; // 予約済みの在庫数
  private final ImmutableList<DomainEvent> domainEvents;

  /**
   * 新しい在庫を作成します
   *
   * @param inventoryId 在庫ID
   * @param productId 商品ID
   * @param initialQuantity 初期在庫数
   * @return 作成された在庫
   */
  public static Inventory create(
      InventoryId inventoryId, ProductId productId, int initialQuantity) {
    if (initialQuantity < 0) {
      throw new IllegalArgumentException("初期在庫数は0以上である必要があります");
    }

    return new Inventory(
        inventoryId,
        productId,
        initialQuantity,
        0,
        ImmutableList.of(new InventoryCreated(inventoryId, productId, initialQuantity)));
  }

  /**
   * 既存の在庫を再構築します（イベントなし）
   *
   * @param id 在庫ID
   * @param productId 商品ID
   * @param availableQuantity 利用可能な在庫数
   * @param reservedQuantity 予約済みの在庫数
   * @return 再構築された在庫
   */
  public static Inventory reconstruct(
      InventoryId id, ProductId productId, int availableQuantity, int reservedQuantity) {
    return new Inventory(id, productId, availableQuantity, reservedQuantity, ImmutableList.of());
  }

  /**
   * 在庫数を調整します
   *
   * @param quantityDelta 変更量（正の値は入荷、負の値は出荷）
   * @return 更新された在庫
   */
  public Inventory adjustQuantity(int quantityDelta) {
    var newAvailableQuantity = this.availableQuantity + quantityDelta;

    if (newAvailableQuantity < 0) {
      throw new IllegalArgumentException("在庫数は0未満にできません");
    }

    DomainEvent event =
        new InventoryAdjusted(this.id, this.productId, quantityDelta, newAvailableQuantity);

    @Var var events = ImmutableList.<DomainEvent>of(event);

    // 在庫切れになった場合は追加イベント
    if (this.availableQuantity > 0 && newAvailableQuantity == 0) {
      events =
          ImmutableList.<DomainEvent>builder()
              .addAll(events)
              .add(new StockDepleted(this.id, this.productId))
              .build();
    }

    return new Inventory(
        this.id, this.productId, newAvailableQuantity, this.reservedQuantity, events);
  }

  /**
   * 在庫を予約します
   *
   * @param quantity 予約する数量
   * @return 更新された在庫
   */
  public Inventory reserve(int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("予約数量は正の値である必要があります");
    }

    if (quantity > this.availableQuantity) {
      throw new InsufficientStockException(
          "予約に必要な在庫が不足しています。利用可能: " + this.availableQuantity + ", 要求: " + quantity);
    }

    var newAvailableQuantity = this.availableQuantity - quantity;
    var newReservedQuantity = this.reservedQuantity + quantity;

    return new Inventory(
        this.id,
        this.productId,
        newAvailableQuantity,
        newReservedQuantity,
        ImmutableList.of(
            new StockReserved(
                this.id, this.productId, quantity, newAvailableQuantity, newReservedQuantity)));
  }

  /**
   * 在庫予約を解除します
   *
   * @param quantity 解除する数量
   * @return 更新された在庫
   */
  public Inventory releaseReservation(int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("解除する数量は正の値である必要があります");
    }

    if (quantity > this.reservedQuantity) {
      throw new IllegalArgumentException(
          "解除する数量が予約数量を超えています。予約済み: " + this.reservedQuantity + ", 要求: " + quantity);
    }

    var newAvailableQuantity = this.availableQuantity + quantity;
    var newReservedQuantity = this.reservedQuantity - quantity;

    return new Inventory(
        this.id,
        this.productId,
        newAvailableQuantity,
        newReservedQuantity,
        ImmutableList.of(
            new StockReleased(
                this.id, this.productId, quantity, newAvailableQuantity, newReservedQuantity)));
  }

  /** 在庫が不足している場合に発生する例外 */
  public static class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
      super(message);
    }
  }

  /** 在庫が作成されたことを示すドメインイベント */
  public record InventoryCreated(InventoryId inventoryId, ProductId productId, int initialQuantity)
      implements DomainEvent {}

  /** 在庫数が調整されたことを示すドメインイベント */
  public record InventoryAdjusted(
      InventoryId inventoryId, ProductId productId, int adjustment, int newQuantity)
      implements DomainEvent {}

  /** 在庫が予約されたことを示すドメインイベント */
  public record StockReserved(
      InventoryId inventoryId,
      ProductId productId,
      int quantity,
      int remainingAvailable,
      int totalReserved)
      implements DomainEvent {}

  /** 在庫予約が解除されたことを示すドメインイベント */
  public record StockReleased(
      InventoryId inventoryId,
      ProductId productId,
      int quantity,
      int newAvailable,
      int remainingReserved)
      implements DomainEvent {}

  /** 在庫切れが発生したことを示すドメインイベント */
  public record StockDepleted(InventoryId inventoryId, ProductId productId)
      implements DomainEvent {}
}
