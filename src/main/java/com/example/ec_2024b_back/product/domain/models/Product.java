package com.example.ec_2024b_back.product.domain.models;

import com.example.ec_2024b_back.product.CategoryId;
import com.example.ec_2024b_back.product.ProductId;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.Var;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.event.types.DomainEvent;

/** 商品エンティティ カタログの中心となる集約ルート */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Product implements AggregateRoot<Product, ProductId> {
  private final ProductId id;
  private final String name;
  private final String description;
  private final BigDecimal basePrice;
  private final String sku; // Stock Keeping Unit
  private final ImmutableSet<CategoryId> categories;
  private final ProductStatus status;
  private final ImmutableList<ProductImage> images;
  private final ImmutableList<DomainEvent> domainEvents;

  /** 商品ステータス */
  public enum ProductStatus {
    DRAFT, // 下書き（未公開）
    ACTIVE, // 有効（販売中）
    INACTIVE, // 一時的に無効
    RETIRED // 廃止（完全に販売終了）
  }

  /** 商品画像 */
  public record ProductImage(String url, String altText, boolean isPrimary) {}

  /**
   * 新しい商品を作成します
   *
   * @param productId 商品ID
   * @param name 商品名
   * @param description 商品説明
   * @param basePrice 基本価格
   * @param sku SKU
   * @param categories カテゴリID一覧
   * @param images 商品画像一覧
   * @return 作成された商品
   */
  public static Product create(
      UUID productId,
      String name,
      String description,
      BigDecimal basePrice,
      String sku,
      Set<CategoryId> categories,
      List<ProductImage> images) {

    return new Product(
        ProductId.fromUUID(productId),
        name,
        description,
        basePrice,
        sku,
        ImmutableSet.copyOf(categories),
        ProductStatus.DRAFT,
        ImmutableList.copyOf(images),
        ImmutableList.of(new ProductCreated(productId, name, sku)));
  }

  /**
   * 既存の商品を再構築します（イベントなし）
   *
   * @param id 商品ID
   * @param name 商品名
   * @param description 商品説明
   * @param basePrice 基本価格
   * @param sku SKU
   * @param categories カテゴリID一覧
   * @param status 商品ステータス
   * @param images 商品画像一覧
   * @return 再構築された商品
   */
  public static Product reconstruct(
      ProductId id,
      String name,
      String description,
      BigDecimal basePrice,
      String sku,
      Set<CategoryId> categories,
      ProductStatus status,
      List<ProductImage> images) {
    return new Product(
        id,
        name,
        description,
        basePrice,
        sku,
        ImmutableSet.copyOf(categories),
        status,
        ImmutableList.copyOf(images),
        ImmutableList.of());
  }

  /**
   * 商品情報を更新します
   *
   * @param name 新しい商品名
   * @param description 新しい商品説明
   * @param basePrice 新しい基本価格
   * @param categories 新しいカテゴリID一覧
   * @param images 新しい商品画像一覧
   * @return 更新された商品
   */
  public Product updateInfo(
      String name,
      String description,
      BigDecimal basePrice,
      Set<CategoryId> categories,
      List<ProductImage> images) {

    return new Product(
        this.id,
        name,
        description,
        basePrice,
        this.sku,
        ImmutableSet.copyOf(categories),
        this.status,
        ImmutableList.copyOf(images),
        ImmutableList.of(new ProductInfoUpdated(this.id.getValue(), name)));
  }

  /**
   * 商品ステータスを変更します
   *
   * @param newStatus 新しいステータス
   * @return 更新された商品
   */
  public Product changeStatus(ProductStatus newStatus) {
    if (this.status == newStatus) {
      return this; // 変更なし
    }

    @Var var events = ImmutableList.<DomainEvent>of();

    if (newStatus == ProductStatus.ACTIVE) {
      events = ImmutableList.of(new ProductActivated(this.id.getValue()));
    } else if (newStatus == ProductStatus.RETIRED) {
      events = ImmutableList.of(new ProductRetired(this.id.getValue()));
    }

    return new Product(
        this.id,
        this.name,
        this.description,
        this.basePrice,
        this.sku,
        this.categories,
        newStatus,
        this.images,
        events);
  }

  /** 商品が作成されたことを示すドメインイベント */
  public record ProductCreated(UUID productId, String name, String sku) implements DomainEvent {}

  /** 商品情報が更新されたことを示すドメインイベント */
  public record ProductInfoUpdated(UUID productId, String name) implements DomainEvent {}

  /** 商品が有効化されたことを示すドメインイベント */
  public record ProductActivated(UUID productId) implements DomainEvent {}

  /** 商品が廃止されたことを示すドメインイベント */
  public record ProductRetired(UUID productId) implements DomainEvent {}
}
