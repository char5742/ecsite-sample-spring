package com.example.ec_2024b_back.shopping.domain.models;

import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.shopping.CartId;
import java.time.Instant;
import org.jmolecules.event.types.DomainEvent;

/** カートに関するドメインイベントを定義します。 */
public sealed interface CartEvent extends DomainEvent {

  /**
   * カートに商品が追加されたイベント
   *
   * @param cartId カートID
   * @param productId 商品ID
   * @param productName 商品名
   * @param quantity 追加された数量
   * @param occurredAt 発生時刻
   */
  record ItemAddedToCart(
      CartId cartId, ProductId productId, String productName, int quantity, Instant occurredAt)
      implements CartEvent {}

  /**
   * カートから商品が削除されたイベント
   *
   * @param cartId カートID
   * @param productId 商品ID
   * @param occurredAt 発生時刻
   */
  record ItemRemovedFromCart(CartId cartId, ProductId productId, Instant occurredAt)
      implements CartEvent {}

  /**
   * カート内の商品数量が変更されたイベント
   *
   * @param cartId カートID
   * @param productId 商品ID
   * @param oldQuantity 以前の数量
   * @param newQuantity 新しい数量
   * @param occurredAt 発生時刻
   */
  record ItemQuantityChanged(
      CartId cartId, ProductId productId, int oldQuantity, int newQuantity, Instant occurredAt)
      implements CartEvent {}

  /**
   * カートがクリアされたイベント
   *
   * @param cartId カートID
   * @param occurredAt 発生時刻
   */
  record CartCleared(CartId cartId, Instant occurredAt) implements CartEvent {}
}
