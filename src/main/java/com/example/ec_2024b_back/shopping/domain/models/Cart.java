package com.example.ec_2024b_back.shopping.domain.models;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.shopping.domain.models.CartEvent.CartCleared;
import com.example.ec_2024b_back.shopping.domain.models.CartEvent.ItemAddedToCart;
import com.example.ec_2024b_back.shopping.domain.models.CartEvent.ItemQuantityChanged;
import com.example.ec_2024b_back.shopping.domain.models.CartEvent.ItemRemovedFromCart;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jspecify.annotations.Nullable;

/** ショッピングカートを表す集約ルート */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Cart implements AggregateRoot<Cart, CartId> {
  private final CartId id;
  private final AccountId accountId;
  private final List<CartItem> items;
  private final List<CartEvent> events;
  private final Instant createdAt;
  private final Instant updatedAt;

  /**
   * 新しいカートを作成します
   *
   * @param id カートID
   * @param accountId アカウントID
   * @param now 現在時刻
   * @return 作成されたカート
   */
  public static Cart create(CartId id, AccountId accountId, Instant now) {
    return new Cart(id, accountId, Collections.emptyList(), Collections.emptyList(), now, now);
  }

  /**
   * データストアからカートを復元します
   *
   * @param id カートID
   * @param accountId アカウントID
   * @param items カート内アイテム
   * @param createdAt 作成日時
   * @param updatedAt 更新日時
   * @return 復元されたカート
   */
  public static Cart reconstruct(
      CartId id, AccountId accountId, List<CartItem> items, Instant createdAt, Instant updatedAt) {
    return new Cart(id, accountId, items, Collections.emptyList(), createdAt, updatedAt);
  }

  /**
   * カートに商品を追加します
   *
   * @param productId 商品ID
   * @param productName 商品名
   * @param unitPrice 単価
   * @param quantity 数量
   * @param now 現在時刻
   * @return 更新されたカート
   */
  public Cart addItem(
      ProductId productId, String productName, BigDecimal unitPrice, int quantity, Instant now) {

    if (quantity <= 0) {
      throw new IllegalArgumentException("数量は1以上でなければなりません");
    }

    var newItems = new ArrayList<>(this.items);
    var newEvents = new ArrayList<>(this.events);

    // 既に同じ商品がカートにあるか確認
    var existingItem = findItemByProductId(productId);

    if (existingItem.isPresent()) {
      // 既存アイテムの数量を更新
      var item = existingItem.get();
      var oldQuantity = item.quantity();
      var newQuantity = oldQuantity + quantity;

      // 既存アイテムを削除して新しいアイテムを追加
      newItems.removeIf(i -> i.productId().equals(productId));
      newItems.add(item.withQuantity(newQuantity));

      newEvents.add(new ItemQuantityChanged(id, productId, oldQuantity, newQuantity, now));
    } else {
      // 新しいアイテムをカートに追加
      var newItem = new CartItem(productId, productName, quantity, unitPrice);
      newItems.add(newItem);
      newEvents.add(new ItemAddedToCart(id, productId, productName, quantity, now));
    }

    return new Cart(
        id,
        accountId,
        Collections.unmodifiableList(newItems),
        Collections.unmodifiableList(newEvents),
        createdAt,
        now);
  }

  /**
   * カートから商品を削除します
   *
   * @param productId 商品ID
   * @param now 現在時刻
   * @return 更新されたカート
   */
  public Cart removeItem(ProductId productId, Instant now) {
    var existingItem = findItemByProductId(productId);

    if (existingItem.isEmpty()) {
      return this; // 変更なし
    }

    var newItems = new ArrayList<>(this.items);
    var newEvents = new ArrayList<>(this.events);

    newItems.removeIf(item -> item.productId().equals(productId));
    newEvents.add(new ItemRemovedFromCart(id, productId, now));

    return new Cart(
        id,
        accountId,
        Collections.unmodifiableList(newItems),
        Collections.unmodifiableList(newEvents),
        createdAt,
        now);
  }

  /**
   * カート内の商品数量を更新します
   *
   * @param productId 商品ID
   * @param newQuantity 新しい数量
   * @param now 現在時刻
   * @return 更新されたカート
   */
  public Cart updateItemQuantity(ProductId productId, int newQuantity, Instant now) {
    if (newQuantity <= 0) {
      // 数量が0以下の場合は商品を削除
      return removeItem(productId, now);
    }

    var existingItem = findItemByProductId(productId);

    if (existingItem.isEmpty()) {
      return this; // 変更なし
    }

    var item = existingItem.get();
    var oldQuantity = item.quantity();

    if (oldQuantity == newQuantity) {
      return this; // 変更なし
    }

    var newItems = new ArrayList<>(this.items);
    var newEvents = new ArrayList<>(this.events);

    // 既存アイテムを削除して新しいアイテムを追加
    newItems.removeIf(i -> i.productId().equals(productId));
    newItems.add(item.withQuantity(newQuantity));

    newEvents.add(new ItemQuantityChanged(id, productId, oldQuantity, newQuantity, now));

    return new Cart(
        id,
        accountId,
        Collections.unmodifiableList(newItems),
        Collections.unmodifiableList(newEvents),
        createdAt,
        now);
  }

  /**
   * カートを空にします
   *
   * @param now 現在時刻
   * @return 更新されたカート
   */
  public Cart clear(Instant now) {
    if (this.items.isEmpty()) {
      return this; // 変更なし
    }

    var newEvents = new ArrayList<>(this.events);
    newEvents.add(new CartCleared(id, now));

    return new Cart(
        id,
        accountId,
        Collections.emptyList(),
        Collections.unmodifiableList(newEvents),
        createdAt,
        now);
  }

  /**
   * カート内の合計金額を計算します
   *
   * @return 合計金額
   */
  public BigDecimal calculateTotal() {
    return items.stream()
        .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * 商品IDを指定してカート内のアイテムを検索します
   *
   * @param productId 商品ID
   * @return 見つかったアイテム（存在しない場合は空）
   */
  public Optional<CartItem> findItemByProductId(ProductId productId) {
    return items.stream().filter(item -> item.productId().equals(productId)).findFirst();
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Cart cart)) {
      return false;
    }

    return Objects.equals(id, cart.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Cart{"
        + "id="
        + id
        + ", accountId="
        + accountId
        + ", items="
        + items
        + ", events="
        + events
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + '}';
  }
}
