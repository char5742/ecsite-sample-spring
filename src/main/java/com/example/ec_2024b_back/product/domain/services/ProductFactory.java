package com.example.ec_2024b_back.product.domain.services;

import com.example.ec_2024b_back.product.CategoryId;
import com.example.ec_2024b_back.product.domain.models.Product;
import com.example.ec_2024b_back.product.domain.models.Product.ProductImage;
import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 商品を作成するファクトリー */
@Component
@RequiredArgsConstructor
public class ProductFactory {
  private final IdGenerator idGen;

  /**
   * 新しい商品を作成します
   *
   * @param name 商品名
   * @param description 商品説明
   * @param basePrice 基本価格
   * @param sku SKU
   * @param categories カテゴリID一覧
   * @param images 商品画像一覧
   * @return 作成された商品
   */
  public Product create(
      String name,
      String description,
      BigDecimal basePrice,
      String sku,
      Set<CategoryId> categories,
      List<ProductImage> images) {
    return Product.create(idGen.newId(), name, description, basePrice, sku, categories, images);
  }
}
