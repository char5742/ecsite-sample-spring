package com.example.ec_2024b_back.product.domain.repositories;

import com.example.ec_2024b_back.product.CategoryId;
import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.product.domain.models.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** 商品リポジトリインターフェース */
public interface Products {

  /**
   * IDによって商品を検索します
   *
   * @param id 商品ID
   * @return 商品を含むMono、見つからない場合は empty
   */
  Mono<Product> findById(ProductId id);

  /**
   * SKUによって商品を検索します
   *
   * @param sku SKU (Stock Keeping Unit)
   * @return 商品を含むMono、見つからない場合は empty
   */
  Mono<Product> findBySku(String sku);

  /**
   * カテゴリに属する商品を検索します
   *
   * @param categoryId カテゴリID
   * @return 商品のFlux
   */
  Flux<Product> findByCategory(CategoryId categoryId);

  /**
   * キーワードで商品を検索します
   *
   * @param keyword 検索キーワード
   * @return 商品のFlux
   */
  Flux<Product> search(String keyword);

  /**
   * 商品を保存します
   *
   * @param product 保存する商品
   * @return 保存された商品を含むMono
   */
  Mono<Product> save(Product product);
}
