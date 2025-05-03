package com.example.ec_2024b_back.auth;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

public record AccountId(UUID id) implements Identifier {
  public static AccountId of(String id) {
    return new AccountId(UUID.fromString(id));
  }
}
