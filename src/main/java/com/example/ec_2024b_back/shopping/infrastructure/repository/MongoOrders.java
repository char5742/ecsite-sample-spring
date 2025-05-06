package com.example.ec_2024b_back.shopping.infrastructure.repository;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.OrderId;
import com.example.ec_2024b_back.shopping.domain.models.Order;
import com.example.ec_2024b_back.shopping.domain.models.OrderStatus;
import com.example.ec_2024b_back.shopping.domain.repositories.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** 注文リポジトリのMongoDBによる実装 */
@Component
@RequiredArgsConstructor
public class MongoOrders implements Orders {

  private final OrderDocumentRepository repository;

  @Override
  public Mono<Order> findById(OrderId id) {
    return repository.findById(id.toString()).map(OrderDocument::toDomain);
  }

  @Override
  public Flux<Order> findByAccountId(AccountId accountId) {
    return repository.findByAccountId(accountId.toString()).map(OrderDocument::toDomain);
  }

  @Override
  public Flux<Order> findByAccountIdAndStatus(AccountId accountId, OrderStatus status) {
    return repository
        .findByAccountIdAndStatus(accountId.toString(), status.name())
        .map(OrderDocument::toDomain);
  }

  @Override
  public Flux<Order> findByStatus(OrderStatus status) {
    return repository.findByStatus(status.name()).map(OrderDocument::toDomain);
  }

  @Override
  public Mono<Order> save(Order order) {
    OrderDocument document = OrderDocument.fromDomain(order);
    return repository.save(document).map(OrderDocument::toDomain);
  }
}
