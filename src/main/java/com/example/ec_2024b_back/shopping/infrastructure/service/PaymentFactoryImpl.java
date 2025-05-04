package com.example.ec_2024b_back.shopping.infrastructure.service;

import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.shopping.domain.services.PaymentFactory;
import java.time.Clock;
import org.springframework.stereotype.Service;

/** 支払いファクトリーの実装クラス */
@Service
public class PaymentFactoryImpl extends PaymentFactory {
  public PaymentFactoryImpl(IdGenerator idGenerator) {
    super(idGenerator, Clock.systemUTC());
  }
}
