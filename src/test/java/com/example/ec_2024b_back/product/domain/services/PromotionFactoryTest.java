package com.example.ec_2024b_back.product.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.product.PromotionId;
import com.example.ec_2024b_back.product.domain.models.Promotion;
import com.example.ec_2024b_back.product.domain.models.Promotion.DiscountType;
import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.utils.Fast;
import com.google.common.collect.ImmutableSet;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Fast
class PromotionFactoryTest {

  private IdGenerator idGenerator;
  private PromotionFactory promotionFactory;

  @BeforeEach
  void setUp() {
    idGenerator = mock(IdGenerator.class);
    promotionFactory = new PromotionFactory(idGenerator);
  }

  @Test
  void shouldCreatePromotion_withGeneratedId() {
    // Given
    var uuid = UUID.randomUUID();
    when(idGenerator.newId()).thenReturn(uuid);

    var name = "テストプロモーション";
    var description = "テストプロモーションの説明";
    var discountType = DiscountType.PERCENTAGE;
    var discountValue = new BigDecimal("20.00");
    var startDate = LocalDateTime.now(ZoneId.systemDefault());
    var endDate = LocalDateTime.now(ZoneId.systemDefault()).plusDays(7);

    // When
    var applicableProducts = ImmutableSet.<ProductId>of();
    var promotion =
        promotionFactory.create(
            name, description, discountType, discountValue, startDate, endDate, applicableProducts);

    // Then
    assertThat(promotion).isNotNull();
    assertThat(promotion.getId()).isEqualTo(new PromotionId(uuid));
    assertThat(promotion.getName()).isEqualTo(name);
    assertThat(promotion.getDescription()).isEqualTo(description);
    assertThat(promotion.getDiscountType()).isEqualTo(discountType);
    assertThat(promotion.getDiscountValue()).isEqualTo(discountValue);
    assertThat(promotion.getStartDateTime()).isEqualTo(startDate);
    assertThat(promotion.getEndDateTime()).isEqualTo(endDate);
    assertThat(promotion.getDomainEvents()).hasSize(1);
    assertThat(promotion.getDomainEvents().get(0)).isInstanceOf(Promotion.PromotionCreated.class);
  }
}
