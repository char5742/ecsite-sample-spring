package com.example.ec_2024b_back.userprofile.domain.models;

import com.example.ec_2024b_back.userprofile.domain.models.UserProfile.UserProfileId;
import com.google.common.collect.ImmutableList;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Identifier;
import org.jmolecules.event.types.DomainEvent;
import org.jspecify.annotations.Nullable;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserProfile implements AggregateRoot<UserProfile, UserProfileId> {
  private final UserProfileId id;
  private final String name;
  private final ImmutableList<Address> addresses;
  private final ImmutableList<DomainEvent> domainEvents;

  public static UserProfile create(UUID userProfileId, String name) {
    if (name.isBlank()) {
      throw new IllegalArgumentException("名前は空であってはなりません");
    }

    return new UserProfile(
        new UserProfileId(userProfileId),
        name,
        ImmutableList.of(),
        ImmutableList.of(new UserProfileCreated(userProfileId.toString(), name)));
  }

  public static UserProfile reconstruct(
      UserProfileId id, String name, ImmutableList<Address> addresses) {
    if (name.isBlank()) {
      throw new IllegalArgumentException("名前は空であってはなりません");
    }

    return new UserProfile(id, name, addresses, ImmutableList.of());
  }

  public UserProfile updateName(String newName) {
    if (newName.isBlank()) {
      throw new IllegalArgumentException("名前は空であってはなりません");
    }

    return new UserProfile(
        id,
        newName,
        addresses,
        ImmutableList.<DomainEvent>builder()
            .addAll(domainEvents)
            .add(new UserProfileUpdated(id.id.toString(), newName))
            .build());
  }

  public UserProfile addAddress(Address address) {
    // 同じIDの住所が既に存在する場合は例外をスロー
    addresses.stream()
        .filter(a -> a.id().equals(address.id()))
        .findAny()
        .ifPresent(
            a -> {
              throw new IllegalArgumentException("住所ID: " + address.id() + " は既に存在します");
            });

    // デフォルト住所の場合、他のデフォルト住所をfalseに設定
    ImmutableList<Address> updatedAddresses;
    if (address.isDefault()) {
      updatedAddresses =
          addresses.stream()
              .map(a -> a.isDefault() ? a.withDefault(false) : a)
              .collect(ImmutableList.toImmutableList());
    } else {
      updatedAddresses = addresses;
    }

    return new UserProfile(
        id,
        name,
        ImmutableList.<Address>builder().addAll(updatedAddresses).add(address).build(),
        ImmutableList.<DomainEvent>builder()
            .addAll(domainEvents)
            .add(new AddressAdded(id.id.toString(), address.id().toString()))
            .build());
  }

  public UserProfile removeAddress(AddressId addressId) {
    // 指定されたIDの住所が存在するか確認
    var addressFound = addresses.stream().anyMatch(a -> a.id().equals(addressId));

    if (!addressFound) {
      throw new IllegalArgumentException("住所ID: " + addressId + " は見つかりません");
    }

    return new UserProfile(
        id,
        name,
        addresses.stream()
            .filter(a -> !a.id().equals(addressId))
            .collect(ImmutableList.toImmutableList()),
        ImmutableList.<DomainEvent>builder()
            .addAll(domainEvents)
            .add(new AddressRemoved(id.id.toString(), addressId.toString()))
            .build());
  }

  public @Nullable Address findDefaultAddress() {
    return addresses.stream().filter(Address::isDefault).findFirst().orElse(null);
  }

  public @Nullable Address findAddressById(AddressId addressId) {
    return addresses.stream().filter(a -> a.id().equals(addressId)).findFirst().orElse(null);
  }

  public record UserProfileId(UUID id) implements Identifier {
    public static UserProfileId of(String id) {
      return new UserProfileId(UUID.fromString(id));
    }

    @Override
    public String toString() {
      return id.toString();
    }
  }

  public record UserProfileCreated(String userProfileId, String name) implements DomainEvent {}

  public record UserProfileUpdated(String userProfileId, String name) implements DomainEvent {}

  public record AddressAdded(String userProfileId, String addressId) implements DomainEvent {}

  public record AddressRemoved(String userProfileId, String addressId) implements DomainEvent {}
}
