package com.example.ec_2024b_back.shopping.infrastructure.repository;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.shopping.CartId;
import com.example.ec_2024b_back.shopping.domain.models.Cart;
import com.example.ec_2024b_back.shopping.domain.models.CartItem;
import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** カートのドキュメントクラス */
@Document(collection = "carts")
public record CartDocument(
    @Id String id,
    @Indexed String accountId,
    ImmutableList<CartItemDocument> items,
    Instant createdAt,
    Instant updatedAt) {

  /** SpringData用のNo-argコンストラクタ */
  public CartDocument() {
    this("", "", ImmutableList.of(), Instant.now(), Instant.now());
  }

  /** ドメインモデルからドキュメントに変換 */
  public static CartDocument fromDomain(Cart cart) {
    ImmutableList<CartItemDocument> itemDocuments =
        cart.getItems().stream()
            .map(CartItemDocument::fromDomain)
            .collect(ImmutableList.toImmutableList());

    return new CartDocument(
        cart.getId().toString(),
        cart.getAccountId().toString(),
        itemDocuments,
        cart.getCreatedAt(),
        cart.getUpdatedAt());
  }

  /** ドキュメントからドメインモデルに変換 */
  public Cart toDomain() {
    ImmutableList<CartItem> domainItems =
        items.stream().map(CartItemDocument::toDomain).collect(ImmutableList.toImmutableList());

    return Cart.reconstruct(
        new CartId(UUID.fromString(id)),
        new AccountId(UUID.fromString(accountId)),
        domainItems,
        createdAt,
        updatedAt);
  }

  /** カート内アイテムのドキュメントクラス */
  public record CartItemDocument(
      String productId, String productName, String unitPrice, int quantity) {

    /** SpringData用のNo-argコンストラクタ */
    public CartItemDocument() {
      this("", "", "0", 0);
    }

    /** ドメインモデルからドキュメントに変換 */
    public static CartItemDocument fromDomain(CartItem item) {
      return new CartItemDocument(
          item.productId().toString(),
          item.productName(),
          item.unitPrice().toString(),
          item.quantity());
    }

    /** ドキュメントからドメインモデルに変換 */
    public CartItem toDomain() {
      return new CartItem(
          ProductId.of(productId), productName, quantity, new BigDecimal(unitPrice));
    }
  }
}
