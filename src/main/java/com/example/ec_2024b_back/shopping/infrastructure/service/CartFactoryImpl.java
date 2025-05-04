package com.example.ec_2024b_back.shopping.infrastructure.service;

import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.shopping.domain.services.CartFactory;
import java.time.Clock;
import org.springframework.stereotype.Service;

/** カートファクトリーの実装クラス */
@Service
public class CartFactoryImpl extends CartFactory {
  public CartFactoryImpl(IdGenerator idGenerator) {
    super(idGenerator, Clock.systemUTC());
  }
}
