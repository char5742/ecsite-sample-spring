package com.example.ec_2024b_back.userprofile.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.userprofile.domain.models.AddressId;
import com.example.ec_2024b_back.utils.Fast;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Fast
class AddressFactoryTest {

  private IdGenerator idGenerator;
  private AddressFactory addressFactory;

  @BeforeEach
  void setUp() {
    idGenerator = mock(IdGenerator.class);
    addressFactory = new AddressFactory(idGenerator);
  }

  @Test
  void shouldCreateAddress_withGeneratedId() {
    // Given
    var uuid = UUID.randomUUID();
    when(idGenerator.newId()).thenReturn(uuid);

    var name = "Test Name";
    var postalCode = "123-4567";
    var prefecture = "東京都";
    var city = "渋谷区";
    var street = "代々木1-1-1";
    var building = "マンション101";
    var phoneNumber = "03-1234-5678";
    var isDefault = true;

    // When
    var address =
        addressFactory.create(
            name, postalCode, prefecture, city, street, building, phoneNumber, isDefault);

    // Then
    assertThat(address).isNotNull();
    assertThat(address.id()).isEqualTo(new AddressId(uuid));
    assertThat(address.name()).isEqualTo(name);
    assertThat(address.postalCode()).isEqualTo(postalCode);
    assertThat(address.prefecture()).isEqualTo(prefecture);
    assertThat(address.city()).isEqualTo(city);
    assertThat(address.street()).isEqualTo(street);
    assertThat(address.building()).isEqualTo(building);
    assertThat(address.phoneNumber()).isEqualTo(phoneNumber);
    assertThat(address.isDefault()).isEqualTo(isDefault);
  }

  @Test
  void shouldCreateAddress_withNullBuilding() {
    // Given
    var uuid = UUID.randomUUID();
    when(idGenerator.newId()).thenReturn(uuid);

    var name = "Test Name";
    var postalCode = "123-4567";
    var prefecture = "東京都";
    var city = "渋谷区";
    var street = "代々木1-1-1";
    var phoneNumber = "03-1234-5678";
    var isDefault = true;

    // When
    var address =
        addressFactory.create(
            name, postalCode, prefecture, city, street, null, phoneNumber, isDefault);

    // Then
    assertThat(address).isNotNull();
    assertThat(address.id()).isEqualTo(new AddressId(uuid));
    assertThat(address.building()).isNull();
  }
}
