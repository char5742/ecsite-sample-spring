package com.example.ec_2024b_back.share.infrastructure;

import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class LocalIdGenerator implements IdGenerator {
  @Override
  public UUID newId() {
    return UUID.randomUUID();
  }
}
