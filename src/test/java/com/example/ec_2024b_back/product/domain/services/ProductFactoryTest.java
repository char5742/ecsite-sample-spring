package com.example.ec_2024b_back.product.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.product.CategoryId;
import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.product.domain.models.Product;
import com.example.ec_2024b_back.product.domain.models.Product.ProductImage;
import com.example.ec_2024b_back.product.domain.models.Product.ProductStatus;
import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.utils.Fast;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Fast
class ProductFactoryTest {

  private IdGenerator idGenerator;
  private ProductFactory productFactory;

  @BeforeEach
  void setUp() {
    idGenerator = mock(IdGenerator.class);
    productFactory = new ProductFactory(idGenerator);
  }

  @Test
  void shouldCreateProduct_withGeneratedId() {
    // Given
    var uuid = UUID.randomUUID();
    when(idGenerator.newId()).thenReturn(uuid);

    var name = "テスト商品";
    var description = "テスト商品の説明";
    var basePrice = new BigDecimal("1000");
    var sku = "TEST-001";
    var categories = ImmutableSet.<CategoryId>of();
    var images =
        ImmutableList.of(
            new ProductImage("https://example.com/image.jpg", "テスト商品画像", /* isPrimary= */ true));

    // When
    var product = productFactory.create(name, description, basePrice, sku, categories, images);

    // Then
    assertThat(product).isNotNull();
    assertThat(product.getId()).isEqualTo(new ProductId(uuid));
    assertThat(product.getName()).isEqualTo(name);
    assertThat(product.getDescription()).isEqualTo(description);
    assertThat(product.getBasePrice()).isEqualTo(basePrice);
    assertThat(product.getSku()).isEqualTo(sku);
    assertThat(product.getStatus()).isEqualTo(ProductStatus.DRAFT);
    assertThat(product.getDomainEvents()).hasSize(1);
    assertThat(product.getDomainEvents().get(0)).isInstanceOf(Product.ProductCreated.class);
  }
}
