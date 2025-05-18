package com.example.ec_2024b_back.shopping.infrastructure.repository;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.OrderId;
import com.example.ec_2024b_back.shopping.PaymentId;
import com.example.ec_2024b_back.shopping.domain.models.Order;
import com.example.ec_2024b_back.shopping.domain.models.OrderItem;
import com.example.ec_2024b_back.shopping.domain.models.OrderStatus;
import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** 注文のドキュメントクラス */
@Document(collection = "orders")
public record OrderDocument(
    @Id String id,
    @Indexed String accountId,
    ImmutableList<OrderItemDocument> items,
    String subtotal,
    String tax,
    String shippingCost,
    String totalAmount,
    @Indexed String status,
    String shippingAddress,
    @Nullable String trackingNumber,
    @Nullable String paymentId,
    @Nullable String paymentMethod,
    Instant createdAt,
    Instant updatedAt) {

  /** SpringData用のNo-argコンストラクタ */
  public OrderDocument() {
    this(
        "",
        "",
        ImmutableList.of(),
        "0",
        "0",
        "0",
        "0",
        OrderStatus.CREATED.name(),
        "",
        null,
        null,
        null,
        Instant.now(),
        Instant.now());
  }

  /** ドメインモデルからドキュメントに変換 */
  public static OrderDocument fromDomain(Order order) {
    ImmutableList<OrderItemDocument> itemDocuments =
        order.getItems().stream()
            .map(OrderItemDocument::fromDomain)
            .collect(ImmutableList.toImmutableList());

    return new OrderDocument(
        order.getId().toString(),
        order.getAccountId().toString(),
        itemDocuments,
        order.getSubtotal().toString(),
        order.getTax().toString(),
        order.getShippingCost().toString(),
        order.getTotalAmount().toString(),
        order.getStatus().name(),
        order.getShippingAddress(),
        order.getTrackingNumber(),
        order.getPaymentId() != null ? order.getPaymentId().toString() : null,
        order.getPaymentMethod(),
        order.getCreatedAt(),
        order.getUpdatedAt());
  }

  /** ドキュメントからドメインモデルに変換 */
  public Order toDomain() {
    ImmutableList<OrderItem> domainItems =
        items.stream().map(OrderItemDocument::toDomain).collect(ImmutableList.toImmutableList());

    PaymentId paymentIdObj = paymentId != null ? new PaymentId(UUID.fromString(paymentId)) : null;

    return Order.reconstruct(
        new OrderId(UUID.fromString(id)),
        new AccountId(UUID.fromString(accountId)),
        domainItems,
        new BigDecimal(subtotal),
        new BigDecimal(tax),
        new BigDecimal(shippingCost),
        new BigDecimal(totalAmount),
        OrderStatus.valueOf(status),
        shippingAddress,
        trackingNumber,
        paymentIdObj,
        paymentMethod,
        createdAt,
        updatedAt);
  }

  /** 注文アイテムのドキュメントクラス */
  public record OrderItemDocument(
      String productId, String productName, String unitPrice, int quantity) {

    /** SpringData用のNo-argコンストラクタ */
    public OrderItemDocument() {
      this("", "", "0", 0);
    }

    /** ドメインモデルからドキュメントに変換 */
    public static OrderItemDocument fromDomain(OrderItem item) {
      return new OrderItemDocument(
          item.productId().toString(),
          item.productName(),
          item.unitPrice().toString(),
          item.quantity());
    }

    /** ドキュメントからドメインモデルに変換 */
    public OrderItem toDomain() {
      return OrderItem.fromDatabase(
          UUID.fromString(productId), productName, new BigDecimal(unitPrice), quantity);
    }
  }
}
