package com.example.ec_2024b_back.share.infrastructure;

import com.example.ec_2024b_back.share.domain.services.TimeProvider;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

@Component
public class LocalTimeProvider implements TimeProvider {
  @Override
  public LocalDateTime now() {
    return LocalDateTime.now(ZoneId.systemDefault());
  }
}
