package com.example.ec_2024b_back.product.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.product.InventoryId;
import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.product.domain.models.Inventory;
import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.utils.Fast;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Fast
class InventoryFactoryTest {

  private IdGenerator idGenerator;
  private InventoryFactory inventoryFactory;

  @BeforeEach
  void setUp() {
    idGenerator = mock(IdGenerator.class);
    inventoryFactory = new InventoryFactory(idGenerator);
  }

  @Test
  void shouldCreateInventory_withGeneratedId() {
    // Given
    var uuid = UUID.randomUUID();
    when(idGenerator.newId()).thenReturn(uuid);

    var productId = new ProductId(UUID.randomUUID());
    var initialQuantity = 10;

    // When
    var inventory = inventoryFactory.create(productId, initialQuantity);

    // Then
    assertThat(inventory).isNotNull();
    assertThat(inventory.getId()).isEqualTo(new InventoryId(uuid));
    assertThat(inventory.getProductId()).isEqualTo(productId);
    assertThat(inventory.getAvailableQuantity()).isEqualTo(initialQuantity);
    assertThat(inventory.getDomainEvents()).hasSize(1);
    assertThat(inventory.getDomainEvents().iterator().next())
        .isInstanceOf(Inventory.InventoryCreated.class);
  }
}
