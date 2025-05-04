package com.example.ec_2024b_back.shopping.domain.models;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.shopping.domain.models.OrderEvent.OrderCancelled;
import com.example.ec_2024b_back.shopping.domain.models.OrderEvent.OrderCompleted;
import com.example.ec_2024b_back.shopping.domain.models.OrderEvent.OrderDelivered;
import com.example.ec_2024b_back.shopping.domain.models.OrderEvent.OrderPaid;
import com.example.ec_2024b_back.shopping.domain.models.OrderEvent.OrderPlaced;
import com.example.ec_2024b_back.shopping.domain.models.OrderEvent.OrderShipped;
import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jspecify.annotations.Nullable;

/** 注文を表す集約ルート */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Order implements AggregateRoot<Order, OrderId> {
  private final OrderId id;
  private final AccountId accountId;
  private final List<OrderItem> items;
  private final BigDecimal subtotal;
  private final BigDecimal tax;
  private final BigDecimal shippingCost;
  private final BigDecimal totalAmount;
  private final OrderStatus status;
  private final String shippingAddress;
  private final @Nullable String trackingNumber;
  private final @Nullable PaymentId paymentId;
  private final @Nullable String paymentMethod;
  private final List<OrderEvent> events;
  private final Instant createdAt;
  private final Instant updatedAt;

  /**
   * カートから新しい注文を作成します
   *
   * @param id 注文ID
   * @param cart カート
   * @param shippingAddress 配送先住所
   * @param shippingCost 配送料
   * @param taxRate 税率（例: 0.1 = 10%）
   * @param now 現在時刻
   * @return 作成された注文
   */
  public static Order createFromCart(
      OrderId id,
      Cart cart,
      String shippingAddress,
      BigDecimal shippingCost,
      BigDecimal taxRate,
      Instant now) {

    Objects.requireNonNull(id, "注文IDは必須です");
    Objects.requireNonNull(cart, "カートは必須です");
    Objects.requireNonNull(shippingAddress, "配送先住所は必須です");
    Objects.requireNonNull(shippingCost, "配送料は必須です");
    Objects.requireNonNull(taxRate, "税率は必須です");

    if (cart.getItems().isEmpty()) {
      throw new IllegalArgumentException("空のカートから注文を作成できません");
    }

    if (shippingAddress.isBlank()) {
      throw new IllegalArgumentException("配送先住所は空白であってはなりません");
    }

    if (shippingCost.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("配送料は0以上でなければなりません");
    }

    if (taxRate.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("税率は0以上でなければなりません");
    }

    // カートアイテムから注文アイテムへの変換
    List<OrderItem> orderItems = cart.getItems().stream().map(OrderItem::fromCartItem).toList();

    // 小計の計算
    BigDecimal subtotal = cart.calculateTotal();

    // 税額の計算
    var tax = subtotal.multiply(taxRate).setScale(0, BigDecimal.ROUND_HALF_UP);

    // 合計金額の計算
    var totalAmount = subtotal.add(tax).add(shippingCost);

    // イベント作成
    List<OrderEvent> events = new ArrayList<>();
    events.add(
        new OrderPlaced(
            id,
            cart.getAccountId(),
            ImmutableList.copyOf(orderItems),
            totalAmount,
            shippingAddress,
            now));

    return new Order(
        id,
        cart.getAccountId(),
        Collections.unmodifiableList(orderItems),
        subtotal,
        tax,
        shippingCost,
        totalAmount,
        OrderStatus.CREATED,
        shippingAddress,
        null,
        null,
        null,
        Collections.unmodifiableList(events),
        now,
        now);
  }

  /**
   * データストアから注文を復元します
   *
   * @param id 注文ID
   * @param accountId アカウントID
   * @param items 注文アイテム
   * @param subtotal 小計
   * @param tax 税額
   * @param shippingCost 配送料
   * @param totalAmount 合計金額
   * @param status 注文状態
   * @param shippingAddress 配送先住所
   * @param trackingNumber 追跡番号
   * @param paymentId 支払いID
   * @param paymentMethod 支払い方法
   * @param createdAt 作成日時
   * @param updatedAt 更新日時
   * @return 復元された注文
   */
  @SuppressWarnings("TooManyParameters")
  public static Order reconstruct(
      OrderId id,
      AccountId accountId,
      List<OrderItem> items,
      BigDecimal subtotal,
      BigDecimal tax,
      BigDecimal shippingCost,
      BigDecimal totalAmount,
      OrderStatus status,
      String shippingAddress,
      @Nullable String trackingNumber,
      @Nullable PaymentId paymentId,
      @Nullable String paymentMethod,
      Instant createdAt,
      Instant updatedAt) {
    return new Order(
        id,
        accountId,
        items,
        subtotal,
        tax,
        shippingCost,
        totalAmount,
        status,
        shippingAddress,
        trackingNumber,
        paymentId,
        paymentMethod,
        Collections.emptyList(), // 復元時は空のイベントリスト
        createdAt,
        updatedAt);
  }

  /**
   * 注文をキャンセルします
   *
   * @param reason キャンセル理由
   * @param now 現在時刻
   * @return 更新された注文
   * @throws DomainException 注文をキャンセルできない状態の場合
   */
  public Order cancel(String reason, Instant now) {
    Objects.requireNonNull(reason, "キャンセル理由は必須です");

    if (reason.isBlank()) {
      throw new IllegalArgumentException("キャンセル理由は空白であってはなりません");
    }

    if (!status.canTransitionTo(OrderStatus.CANCELLED)) {
      throw new DomainException("現在の状態 " + status + " の注文はキャンセルできません");
    }

    List<OrderEvent> newEvents = new ArrayList<>(this.events);
    newEvents.add(new OrderCancelled(id, reason, now));

    return new Order(
        id,
        accountId,
        items,
        subtotal,
        tax,
        shippingCost,
        totalAmount,
        OrderStatus.CANCELLED,
        shippingAddress,
        trackingNumber,
        paymentId,
        paymentMethod,
        Collections.unmodifiableList(newEvents),
        createdAt,
        now);
  }

  /**
   * 注文を支払い済み状態に更新します
   *
   * @param paymentId 支払いID
   * @param paymentMethod 支払い方法
   * @param now 現在時刻
   * @return 更新された注文
   * @throws DomainException 注文の状態更新ができない場合
   */
  public Order markPaid(PaymentId paymentId, String paymentMethod, Instant now) {
    Objects.requireNonNull(paymentId, "支払いIDは必須です");
    Objects.requireNonNull(paymentMethod, "支払い方法は必須です");

    if (paymentMethod.isBlank()) {
      throw new IllegalArgumentException("支払い方法は空白であってはなりません");
    }

    if (!status.canTransitionTo(OrderStatus.PAID)) {
      throw new DomainException("現在の状態 " + status + " の注文は支払い済みにできません");
    }

    List<OrderEvent> newEvents = new ArrayList<>(this.events);
    newEvents.add(new OrderPaid(id, paymentId, paymentMethod, totalAmount, now));

    return new Order(
        id,
        accountId,
        items,
        subtotal,
        tax,
        shippingCost,
        totalAmount,
        OrderStatus.PAID,
        shippingAddress,
        trackingNumber,
        paymentId,
        paymentMethod,
        Collections.unmodifiableList(newEvents),
        createdAt,
        now);
  }

  /**
   * 注文を出荷済み状態に更新します
   *
   * @param trackingNumber 追跡番号
   * @param shippingMethod 配送方法
   * @param now 現在時刻
   * @return 更新された注文
   * @throws DomainException 注文の状態更新ができない場合
   */
  public Order markShipped(@Nullable String trackingNumber, String shippingMethod, Instant now) {
    Objects.requireNonNull(shippingMethod, "配送方法は必須です");

    if (shippingMethod.isBlank()) {
      throw new IllegalArgumentException("配送方法は空白であってはなりません");
    }

    if (!status.canTransitionTo(OrderStatus.SHIPPED)) {
      throw new DomainException("現在の状態 " + status + " の注文は出荷済みにできません");
    }

    List<OrderEvent> newEvents = new ArrayList<>(this.events);
    newEvents.add(new OrderShipped(id, trackingNumber, shippingMethod, now));

    return new Order(
        id,
        accountId,
        items,
        subtotal,
        tax,
        shippingCost,
        totalAmount,
        OrderStatus.SHIPPED,
        shippingAddress,
        trackingNumber,
        paymentId,
        paymentMethod,
        Collections.unmodifiableList(newEvents),
        createdAt,
        now);
  }

  /**
   * 注文を配送完了状態に更新します
   *
   * @param deliveredAt 配送完了日時
   * @param now 現在時刻
   * @return 更新された注文
   * @throws DomainException 注文の状態更新ができない場合
   */
  public Order markDelivered(Instant deliveredAt, Instant now) {
    Objects.requireNonNull(deliveredAt, "配送完了日時は必須です");

    if (!status.canTransitionTo(OrderStatus.DELIVERED)) {
      throw new DomainException("現在の状態 " + status + " の注文は配送完了にできません");
    }

    List<OrderEvent> newEvents = new ArrayList<>(this.events);
    newEvents.add(new OrderDelivered(id, deliveredAt, now));

    return new Order(
        id,
        accountId,
        items,
        subtotal,
        tax,
        shippingCost,
        totalAmount,
        OrderStatus.DELIVERED,
        shippingAddress,
        trackingNumber,
        paymentId,
        paymentMethod,
        Collections.unmodifiableList(newEvents),
        createdAt,
        now);
  }

  /**
   * 注文を完了状態に更新します
   *
   * @param now 現在時刻
   * @return 更新された注文
   * @throws DomainException 注文の状態更新ができない場合
   */
  public Order complete(Instant now) {
    if (!status.canTransitionTo(OrderStatus.COMPLETED)) {
      throw new DomainException("現在の状態 " + status + " の注文は完了にできません");
    }

    List<OrderEvent> newEvents = new ArrayList<>(this.events);
    newEvents.add(new OrderCompleted(id, now));

    return new Order(
        id,
        accountId,
        items,
        subtotal,
        tax,
        shippingCost,
        totalAmount,
        OrderStatus.COMPLETED,
        shippingAddress,
        trackingNumber,
        paymentId,
        paymentMethod,
        Collections.unmodifiableList(newEvents),
        createdAt,
        now);
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Order order)) {
      return false;
    }

    return Objects.equals(id, order.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Order{"
        + "id="
        + id
        + ", accountId="
        + accountId
        + ", items="
        + items
        + ", subtotal="
        + subtotal
        + ", tax="
        + tax
        + ", shippingCost="
        + shippingCost
        + ", totalAmount="
        + totalAmount
        + ", status="
        + status
        + ", shippingAddress='"
        + shippingAddress
        + '\''
        + ", trackingNumber='"
        + trackingNumber
        + '\''
        + ", paymentId="
        + paymentId
        + ", paymentMethod='"
        + paymentMethod
        + '\''
        + ", events="
        + events
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + '}';
  }
}
