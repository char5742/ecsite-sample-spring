package com.example.ec_2024b_back.auth.domain.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.ec_2024b_back.utils.Fast;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

@Fast
class AccountTest {
  @Test
  void constructor_shouldCreateInstance_whenValidIdProvided() {
    var id = new Account.AccountId("abc123");
    var account = new Account(id, ImmutableList.of());
    assertThat(account.id()).isEqualTo(id);
  }

  @Test
  void constructor_shouldThrowException_whenBlankIdProvided() {
    assertThatThrownBy(() -> new Account.AccountId(" "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("ID must not be blank");
  }
}
