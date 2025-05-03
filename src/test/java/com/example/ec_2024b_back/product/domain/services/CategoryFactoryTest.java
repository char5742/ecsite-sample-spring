package com.example.ec_2024b_back.product.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.product.CategoryId;
import com.example.ec_2024b_back.product.domain.models.Category;
import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.utils.Fast;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Fast
class CategoryFactoryTest {

  private IdGenerator idGenerator;
  private CategoryFactory categoryFactory;

  @BeforeEach
  void setUp() {
    idGenerator = mock(IdGenerator.class);
    categoryFactory = new CategoryFactory(idGenerator);
  }

  @Test
  void shouldCreateCategory_withGeneratedId() {
    // Given
    var uuid = UUID.randomUUID();
    when(idGenerator.newId()).thenReturn(uuid);

    var name = "テストカテゴリ";
    var description = "テストカテゴリの説明";
    var parentId = new CategoryId(UUID.randomUUID());

    // When
    var category = categoryFactory.create(name, description, parentId);

    // Then
    assertThat(category).isNotNull();
    assertThat(category.getId()).isEqualTo(new CategoryId(uuid));
    assertThat(category.getName()).isEqualTo(name);
    assertThat(category.getDescription()).isEqualTo(description);
    assertThat(category.getParentCategoryId()).isEqualTo(parentId);
    assertThat(category.getDomainEvents()).hasSize(1);
    assertThat(category.getDomainEvents().iterator().next())
        .isInstanceOf(Category.CategoryCreated.class);
  }

  @Test
  void shouldCreateRootCategory_withNullParentId() {
    // Given
    var uuid = UUID.randomUUID();
    when(idGenerator.newId()).thenReturn(uuid);

    var name = "ルートカテゴリ";
    var description = "ルートカテゴリの説明";

    // When
    var category = categoryFactory.createRoot(name, description);

    // Then
    assertThat(category).isNotNull();
    assertThat(category.getId()).isEqualTo(new CategoryId(uuid));
    assertThat(category.getName()).isEqualTo(name);
    assertThat(category.getDescription()).isEqualTo(description);
    assertThat(category.getParentCategoryId()).isNull();
    assertThat(category.getDomainEvents()).hasSize(1);
    assertThat(category.getDomainEvents().iterator().next())
        .isInstanceOf(Category.CategoryCreated.class);
  }
}
