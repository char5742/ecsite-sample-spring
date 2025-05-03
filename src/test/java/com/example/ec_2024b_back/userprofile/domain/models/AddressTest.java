package com.example.ec_2024b_back.userprofile.domain.models;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ec_2024b_back.utils.Fast;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@Fast
class AddressTest {

  @Test
  void shouldCreateAddress_whenValidParameters() {
    // Given
    var uuid = UUID.randomUUID();
    var id = new AddressId(uuid);

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
        new Address(
            id, name, postalCode, prefecture, city, street, building, phoneNumber, isDefault);

    // Then
    assertThat(address.id()).isEqualTo(id);
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
  void shouldCreateAddress_whenBuildingIsNull() {
    // Given
    var uuid = UUID.randomUUID();
    var id = new AddressId(uuid);

    var name = "Test Name";
    var postalCode = "123-4567";
    var prefecture = "東京都";
    var city = "渋谷区";
    var street = "代々木1-1-1";
    var phoneNumber = "03-1234-5678";
    var isDefault = true;

    // When
    var address =
        new Address(id, name, postalCode, prefecture, city, street, null, phoneNumber, isDefault);

    // Then
    assertThat(address.building()).isNull();
  }

  @Test
  void shouldChangeDefaultFlag_whenWithDefaultCalled() {
    // Given
    var uuid = UUID.randomUUID();
    var id = new AddressId(uuid);

    var address =
        new Address(
            id,
            "Test Name",
            "123-4567",
            "東京都",
            "渋谷区",
            "代々木1-1-1",
            "マンション101",
            "03-1234-5678",
            /* isDefault= */ true);

    // When
    var updatedAddress = address.withDefault(false);

    // Then
    assertThat(updatedAddress.isDefault()).isFalse();
    // その他のフィールドは変わっていないことを確認
    assertThat(updatedAddress.id()).isEqualTo(address.id());
    assertThat(updatedAddress.name()).isEqualTo(address.name());
  }

  @Test
  void shouldCreateAddressId_whenValidString() {
    // Given
    var uuidString = "550e8400-e29b-41d4-a716-446655440000";

    // When
    var addressId = AddressId.of(uuidString);

    // Then
    assertThat(addressId.id()).isEqualTo(UUID.fromString(uuidString));
    assertThat(addressId.toString()).isEqualTo(uuidString);
  }
}
