package com.example.ec_2024b_back.shopping.infrastructure.repository;

import com.example.ec_2024b_back.shopping.OrderId;
import com.example.ec_2024b_back.shopping.PaymentId;
import com.example.ec_2024b_back.shopping.domain.models.Payment;
import com.example.ec_2024b_back.shopping.domain.models.PaymentStatus;
import com.example.ec_2024b_back.shopping.domain.repositories.Payments;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** 支払いリポジトリのMongoDB実装 */
@Repository
public class MongoPayments implements Payments {

  private final PaymentDocumentRepository documentRepository;

  public MongoPayments(
      ReactiveMongoTemplate mongoTemplate, PaymentDocumentRepository documentRepository) {

    this.documentRepository = documentRepository;
  }

  @Override
  public Mono<Payment> findById(PaymentId id) {
    return documentRepository.findById(id.id().toString()).map(PaymentDocument::toDomain);
  }

  @Override
  public Mono<Payment> findByOrderId(OrderId orderId) {
    return documentRepository.findByOrderId(orderId.id().toString()).map(PaymentDocument::toDomain);
  }

  @Override
  public Flux<Payment> findByStatus(PaymentStatus status) {
    return documentRepository.findByStatus(status.name()).map(PaymentDocument::toDomain);
  }

  @Override
  public Mono<Payment> save(Payment payment) {
    PaymentDocument document = PaymentDocument.fromDomain(payment);
    return documentRepository.save(document).map(PaymentDocument::toDomain);
  }
}
