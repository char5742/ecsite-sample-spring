package com.example.ec_2024b_back.auth.domain.models;

import com.example.ec_2024b_back.auth.AccountId;
import com.google.common.collect.ImmutableList;
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
  private final ImmutableList<Authentication> authentications;
  private final ImmutableList<DomainEvent> domainEvents;

  public static Account create(UUID accountId, ImmutableList<Authentication> authentications) {
    return new Account(
        new AccountId(accountId),
        authentications,
        ImmutableList.of(new AccountRegistered(accountId.toString())));
  }

  public static Account reconstruct(AccountId id, ImmutableList<Authentication> authentications) {
    return new Account(id, authentications, ImmutableList.of());
  }

  public record AccountRegistered(String accountId) implements DomainEvent {}
}
