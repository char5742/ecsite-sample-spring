package com.example.ec_2024b_back.shopping.infrastructure.repository;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.CartId;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import com.example.ec_2024b_back.shopping.domain.repositories.Carts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** カートリポジトリのMongoDBによる実装 */
@Component
@RequiredArgsConstructor
public class MongoCarts implements Carts {

  private final CartDocumentRepository repository;

  @Override
  public Mono<Cart> findById(CartId id) {
    return repository.findById(id.toString()).map(CartDocument::toDomain);
  }

  @Override
  public Mono<Cart> findByAccountId(AccountId accountId) {
    return repository.findByAccountId(accountId.toString()).map(CartDocument::toDomain);
  }

  @Override
  public Mono<Cart> save(Cart cart) {
    CartDocument document = CartDocument.fromDomain(cart);
    return repository.save(document).map(CartDocument::toDomain);
  }

  @Override
  public Mono<Void> deleteById(CartId id) {
    return repository.deleteById(id.toString());
  }
}
