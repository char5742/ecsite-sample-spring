package com.example.ec_2024b_back.auth.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.auth.domain.models.Authentication;
import com.example.ec_2024b_back.auth.domain.models.EmailAuthentication;
import com.example.ec_2024b_back.share.domain.models.Email;
import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.utils.Fast;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Fast
@ExtendWith(MockitoExtension.class)
class AccountFactoryTest {

  @Mock private IdGenerator idGenerator;

  @InjectMocks private AccountFactory accountFactory;

  private UUID uuid;
  private List<Authentication> authentications;

  @BeforeEach
  void setUp() {
    uuid = UUID.randomUUID();
    // テスト用の認証情報を作成
    var email = new Email("test@example.com");
    var password =
        new EmailAuthentication.HashedPassword(
            "$2a$10$abcdefghijklmnopqrstuvwxyno12345678901234567890123abc");
    var auth = new EmailAuthentication(email, password);
    authentications = List.of(auth);
  }

  @Test
  void create_shouldCreateAccountWithGeneratedId_whenCalled() {
    // Arrange
    when(idGenerator.newId()).thenReturn(uuid);

    // Act
    var account = accountFactory.create(authentications);

    // Assert
    assertThat(account).isNotNull();
    assertThat(account.getId()).isEqualTo(new AccountId(uuid));
    assertThat(account.getAuthentications()).hasSize(1);
    assertThat(account.getAuthentications().get(0)).isEqualTo(authentications.get(0));
    assertThat(account.getDomainEvents()).hasSize(1);
    assertThat(account.getDomainEvents().get(0)).isInstanceOf(account.getClass().getClasses()[0]);
  }

  @Test
  void create_shouldCreateAccountWithEmptyAuthentications_whenCalledWithEmptyList() {
    // Arrange
    when(idGenerator.newId()).thenReturn(uuid);
    var emptyAuthList = List.<Authentication>of();

    // Act
    var account = accountFactory.create(emptyAuthList);

    // Assert
    assertThat(account).isNotNull();
    assertThat(account.getId()).isEqualTo(new AccountId(uuid));
    assertThat(account.getAuthentications()).isEmpty();
    assertThat(account.getDomainEvents()).hasSize(1);
  }
}
