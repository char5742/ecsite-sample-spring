package com.example.ec_2024b_back.product.application.workflow;

import com.example.ec_2024b_back.product.CategoryId;
import com.example.ec_2024b_back.product.domain.models.Product;
import com.example.ec_2024b_back.product.domain.models.Product.ProductImage;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** 商品作成ワークフロー */
public interface CreateProductWorkflow {

  /** SKUの重複をチェックする */
  @FunctionalInterface
  interface CheckSkuUniquenessStep extends Function<Context.Input, Mono<Context.Validated>> {}

  /** カテゴリの存在をチェックする */
  @FunctionalInterface
  interface ValidateCategoriesStep
      extends Function<Context.Validated, Mono<Context.CategoriesValidated>> {}

  /** 商品を作成する */
  @FunctionalInterface
  interface CreateProductStep
      extends Function<Context.CategoriesValidated, Mono<Context.Created>> {}

  /** 在庫を初期化する */
  @FunctionalInterface
  interface InitializeInventoryStep extends Function<Context.Created, Mono<Context.Complete>> {}

  sealed interface Context
      permits Context.Input,
          Context.Validated,
          Context.CategoriesValidated,
          Context.Created,
          Context.Complete {

    record Input(
        String name,
        String description,
        BigDecimal basePrice,
        String sku,
        ImmutableSet<CategoryId> categories,
        ImmutableList<ProductImage> images,
        int initialStock)
        implements Context {}

    record Validated(
        String name,
        String description,
        BigDecimal basePrice,
        String sku,
        ImmutableSet<CategoryId> categories,
        ImmutableList<ProductImage> images,
        int initialStock)
        implements Context {}

    record CategoriesValidated(
        String name,
        String description,
        BigDecimal basePrice,
        String sku,
        ImmutableSet<CategoryId> categories,
        ImmutableList<ProductImage> images,
        int initialStock)
        implements Context {}

    record Created(Product product, int initialStock) implements Context {}

    record Complete(Product product) implements Context {}
  }

  /**
   * 商品作成処理を実行します
   *
   * @param name 商品名
   * @param description 商品説明
   * @param basePrice 基本価格
   * @param sku SKU
   * @param categories カテゴリID一覧
   * @param images 商品画像一覧
   * @param initialStock 初期在庫数
   * @return 作成された商品を含むMono
   */
  Mono<Context.Complete> execute(
      String name,
      String description,
      BigDecimal basePrice,
      String sku,
      Set<CategoryId> categories,
      List<ProductImage> images,
      int initialStock);

  /** SKUが既に存在する場合のカスタム例外 */
  class DuplicateSkuException extends DomainException {
    public DuplicateSkuException(String sku) {
      super("SKU: " + sku + " は既に存在します");
    }
  }

  /** カテゴリが存在しない場合のカスタム例外 */
  class CategoryNotFoundException extends DomainException {
    public CategoryNotFoundException(CategoryId categoryId) {
      super("カテゴリID: " + categoryId.value() + " は存在しません");
    }
  }
}
