package com.example.ec_2024b_back.auth.domain.models;

import com.example.ec_2024b_back.auth.AccountId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.event.types.DomainEvent;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Account implements AggregateRoot<Account, AccountId> {
  private final AccountId id;
  private final List<Authentication> authentications;
  private final List<DomainEvent> domainEvents;

  public static Account create(UUID accountId, List<Authentication> authentications) {
    return new Account(
        new AccountId(accountId),
        new ArrayList<>(authentications),
        List.of(new AccountRegistered(accountId.toString())));
  }

  public static Account reconstruct(AccountId id, List<Authentication> authentications) {
    return new Account(id, new ArrayList<>(authentications), new ArrayList<>());
  }

  public record AccountRegistered(String accountId) implements DomainEvent {}
}
