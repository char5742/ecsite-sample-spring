package com.example.ec_2024b_back.product.domain.services;

import com.example.ec_2024b_back.product.CategoryId;
import com.example.ec_2024b_back.product.domain.models.Category;
import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

/** カテゴリを作成するファクトリー */
@Component
@RequiredArgsConstructor
public class CategoryFactory {
  private final IdGenerator idGen;

  /**
   * 新しいカテゴリを作成します
   *
   * @param name カテゴリ名
   * @param description カテゴリの説明
   * @param parentCategoryId 親カテゴリID (任意)
   * @return 作成されたカテゴリ
   */
  public Category create(String name, String description, @Nullable CategoryId parentCategoryId) {
    return Category.create(new CategoryId(idGen.newId()), name, description, parentCategoryId);
  }

  /**
   * 新しい最上位カテゴリを作成します
   *
   * @param name カテゴリ名
   * @param description カテゴリの説明
   * @return 作成されたカテゴリ
   */
  public Category createRoot(String name, String description) {
    return create(name, description, null);
  }
}
