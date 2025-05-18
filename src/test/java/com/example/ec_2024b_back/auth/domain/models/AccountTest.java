package com.example.ec_2024b_back.auth.domain.models;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.share.domain.models.Email;
import com.example.ec_2024b_back.utils.Fast;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@Fast
class AccountTest {

  @Test
  void create_shouldCreateAccountWithDomainEvent_whenCalledWithValidParams() {
    // Arrange
    var uuid = UUID.randomUUID();
    var email = new Email("test@example.com");
    var password =
        new EmailAuthentication.HashedPassword(
            "$2a$10$abcdefghijklmnopqrstuvwxyno12345678901234567890123abc");
    var auth = new EmailAuthentication(email, password);
    var authentications = List.<Authentication>of(auth);

    // Act
    var account = Account.create(uuid, authentications);

    // Assert
    assertThat(account).isNotNull();
    assertThat(account.getId().id()).isEqualTo(uuid);
    assertThat(account.getAuthentications()).isEqualTo(authentications);

    // ドメインイベントの検証
    assertThat(account.getDomainEvents()).hasSize(1);
    assertThat(account.getDomainEvents().get(0)).isInstanceOf(Account.AccountRegistered.class);
    var event = (Account.AccountRegistered) account.getDomainEvents().get(0);
    assertThat(event.accountId()).isEqualTo(uuid.toString());
  }

  @Test
  void reconstruct_shouldRecreateAccountWithoutEvents_whenCalled() {
    // Arrange
    var uuid = UUID.randomUUID();
    var accountId = new AccountId(uuid);
    var authentications = List.<Authentication>of();

    // Act
    var account = Account.reconstruct(accountId, authentications);

    // Assert
    assertThat(account).isNotNull();
    assertThat(account.getId()).isEqualTo(accountId);
    assertThat(account.getAuthentications()).isEqualTo(authentications);
    assertThat(account.getDomainEvents()).isEmpty();
  }

  @Test
  void reconstruct_shouldRecreateAccountWithAuthentications_whenCalledWithAuths() {
    // Arrange
    var uuid = UUID.randomUUID();
    var accountId = new AccountId(uuid);
    var email = new Email("test@example.com");
    var password =
        new EmailAuthentication.HashedPassword(
            "$2a$10$abcdefghijklmnopqrstuvwxyno12345678901234567890123abc");
    var auth = new EmailAuthentication(email, password);
    var authentications = List.<Authentication>of(auth);

    // Act
    var account = Account.reconstruct(accountId, authentications);

    // Assert
    assertThat(account).isNotNull();
    assertThat(account.getId()).isEqualTo(accountId);
    assertThat(account.getAuthentications()).isEqualTo(authentications);
    assertThat(account.getAuthentications()).hasSize(1);
    assertThat(account.getAuthentications().get(0)).isEqualTo(auth);
    assertThat(account.getDomainEvents()).isEmpty();
  }
}
