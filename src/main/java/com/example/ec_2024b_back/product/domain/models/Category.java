package com.example.ec_2024b_back.product.domain.models;

import com.example.ec_2024b_back.product.CategoryId;
import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jmolecules.ddd.types.Entity;
import org.jmolecules.event.types.DomainEvent;

/** 商品カテゴリ */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Category implements Entity<Product, CategoryId> {
  private final CategoryId id;
  private final String name;
  private final String description;
  private final CategoryId parentCategoryId; // null可（ルートカテゴリの場合）
  private final ImmutableSet<DomainEvent> domainEvents;

  /**
   * 新しいカテゴリを作成します
   *
   * @param categoryId カテゴリID
   * @param name カテゴリ名
   * @param description カテゴリの説明
   * @param parentCategoryId 親カテゴリID（ルートカテゴリの場合はnull）
   * @return 作成されたカテゴリ
   */
  public static Category create(
      UUID categoryId, String name, String description, CategoryId parentCategoryId) {
    return new Category(
        CategoryId.fromUUID(categoryId),
        name,
        description,
        parentCategoryId,
        ImmutableSet.of(new CategoryCreated(categoryId, name)));
  }

  /**
   * 既存のカテゴリを再構築します（イベントなし）
   *
   * @param id カテゴリID
   * @param name カテゴリ名
   * @param description カテゴリの説明
   * @param parentCategoryId 親カテゴリID
   * @return 再構築されたカテゴリ
   */
  public static Category reconstruct(
      CategoryId id, String name, String description, CategoryId parentCategoryId) {
    return new Category(id, name, description, parentCategoryId, ImmutableSet.of());
  }

  /** カテゴリが作成されたことを示すドメインイベント */
  public record CategoryCreated(UUID categoryId, String name) implements DomainEvent {}
}
