package com.example.ec_2024b_back.userprofile.domain.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.ec_2024b_back.utils.Fast;
import com.google.common.collect.ImmutableList;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@Fast
class UserProfileTest {

  @Test
  void shouldCreateUserProfile_whenValidParameters() {
    // Given
    var uuid = UUID.randomUUID();
    var name = "Test User";

    // When
    var userProfile = UserProfile.create(uuid, name);

    // Then
    assertThat(userProfile.getId().toString()).isEqualTo(uuid.toString());
    assertThat(userProfile.getName()).isEqualTo(name);
    assertThat(userProfile.getAddresses()).isEmpty();
    assertThat(userProfile.getDomainEvents()).hasSize(1);
    assertThat(userProfile.getDomainEvents().get(0))
        .isInstanceOf(UserProfile.UserProfileCreated.class);
  }

  @Test
  void shouldUpdateName_whenValidName() {
    // Given
    var uuid = UUID.randomUUID();
    var originalName = "Original Name";
    var newName = "New Name";
    var userProfile = UserProfile.create(uuid, originalName);

    // When
    var updatedUserProfile = userProfile.updateName(newName);

    // Then
    assertThat(updatedUserProfile.getId()).isEqualTo(userProfile.getId());
    assertThat(updatedUserProfile.getName()).isEqualTo(newName);
    assertThat(updatedUserProfile.getDomainEvents()).hasSize(2);
    assertThat(updatedUserProfile.getDomainEvents().get(1))
        .isInstanceOf(UserProfile.UserProfileUpdated.class);
  }

  @Test
  void shouldAddAddress_whenValidAddress() {
    // Given
    var uuid = UUID.randomUUID();
    var name = "Test User";
    var userProfile = UserProfile.create(uuid, name);

    var address =
        new Address(
            "addr-1",
            "Test Name",
            "123-4567",
            "東京都",
            "渋谷区",
            "代々木1-1-1",
            "マンション101",
            "03-1234-5678",
            /* isDefault= */ true);

    // When
    var updatedUserProfile = userProfile.addAddress(address);

    // Then
    assertThat(updatedUserProfile.getAddresses()).hasSize(1);
    assertThat(updatedUserProfile.getAddresses().get(0)).isEqualTo(address);
    assertThat(updatedUserProfile.getDomainEvents()).hasSize(2);
    assertThat(updatedUserProfile.getDomainEvents().get(1))
        .isInstanceOf(UserProfile.AddressAdded.class);
  }

  @Test
  void shouldRemoveAddress_whenAddressExists() {
    // Given
    var uuid = UUID.randomUUID();
    var name = "Test User";

    var address =
        new Address(
            "addr-1",
            "Test Name",
            "123-4567",
            "東京都",
            "渋谷区",
            "代々木1-1-1",
            "マンション101",
            "03-1234-5678",
            /* isDefault= */ true);

    var userProfile =
        UserProfile.reconstruct(
            new UserProfile.UserProfileId(uuid), name, ImmutableList.of(address));

    // When
    var updatedUserProfile = userProfile.removeAddress("addr-1");

    // Then
    assertThat(updatedUserProfile.getAddresses()).isEmpty();
    assertThat(updatedUserProfile.getDomainEvents()).hasSize(1);
    assertThat(updatedUserProfile.getDomainEvents().get(0))
        .isInstanceOf(UserProfile.AddressRemoved.class);
  }

  @Test
  void shouldThrowException_whenRemovingNonExistentAddress() {
    // Given
    var uuid = UUID.randomUUID();
    var name = "Test User";
    var userProfile = UserProfile.create(uuid, name);

    // When/Then
    assertThatThrownBy(() -> userProfile.removeAddress("non-existent"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("見つかりません");
  }
}
