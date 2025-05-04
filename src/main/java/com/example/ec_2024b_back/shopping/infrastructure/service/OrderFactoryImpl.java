package com.example.ec_2024b_back.shopping.infrastructure.service;

import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.shopping.domain.services.OrderFactory;
import java.time.Clock;
import org.springframework.stereotype.Service;

/** 注文ファクトリーの実装クラス */
@Service
public class OrderFactoryImpl extends OrderFactory {
  public OrderFactoryImpl(IdGenerator idGenerator) {
    super(idGenerator, Clock.systemUTC());
  }
}
