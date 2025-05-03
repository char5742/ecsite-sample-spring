package com.example.ec_2024b_back.product.domain.repositories;

import com.example.ec_2024b_back.product.CategoryId;
import com.example.ec_2024b_back.product.domain.models.Category;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** カテゴリリポジトリインターフェース */
public interface Categories {

  /**
   * IDによってカテゴリを検索します
   *
   * @param id カテゴリID
   * @return カテゴリを含むMono、見つからない場合は empty
   */
  Mono<Category> findById(CategoryId id);

  /**
   * 親カテゴリIDに基づいてサブカテゴリを検索します
   *
   * @param parentId 親カテゴリID
   * @return カテゴリのFlux
   */
  Flux<Category> findByParentId(CategoryId parentId);

  /**
   * ルートカテゴリ（親カテゴリを持たないカテゴリ）を検索します
   *
   * @return ルートカテゴリのFlux
   */
  Flux<Category> findRootCategories();

  /**
   * カテゴリを保存します
   *
   * @param category 保存するカテゴリ
   * @return 保存されたカテゴリを含むMono
   */
  Mono<Category> save(Category category);
}
