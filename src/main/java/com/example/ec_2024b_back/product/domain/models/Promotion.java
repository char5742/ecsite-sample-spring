package com.example.ec_2024b_back.product.domain.models;

import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.product.PromotionId;
import com.example.ec_2024b_back.share.domain.services.TimeProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.event.types.DomainEvent;

/** プロモーション集約 商品に適用される割引やキャンペーンを表現します */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Promotion implements AggregateRoot<Promotion, PromotionId> {
  private final PromotionId id;
  private final String name;
  private final String description;
  private final DiscountType discountType;
  private final BigDecimal discountValue;
  private final LocalDateTime startDateTime;
  private final LocalDateTime endDateTime;
  private final boolean isActive;
  private final ImmutableSet<ProductId> applicableProducts; // 適用対象商品（空の場合はすべての商品が対象）
  private final ImmutableList<DomainEvent> domainEvents;

  /** 割引タイプ */
  public enum DiscountType {
    PERCENTAGE, // 割合割引 (例: 10%オフ)
    FIXED_AMOUNT // 固定金額割引 (例: 500円オフ)
  }

  /**
   * 新しいプロモーションを作成します
   *
   * @param promotionId プロモーションID
   * @param name 名前
   * @param description 説明
   * @param discountType 割引タイプ
   * @param discountValue 割引値
   * @param startDateTime 開始日時
   * @param endDateTime 終了日時
   * @param applicableProducts 適用対象商品
   * @return 作成されたプロモーション
   */
  public static Promotion create(
      PromotionId promotionId,
      String name,
      String description,
      DiscountType discountType,
      BigDecimal discountValue,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Set<ProductId> applicableProducts) {

    validateDiscountValue(discountType, discountValue);

    if (endDateTime.isBefore(startDateTime)) {
      throw new IllegalArgumentException("終了日時は開始日時より後である必要があります");
    }

    // 初期状態では非アクティブ
    return new Promotion(
        promotionId,
        name,
        description,
        discountType,
        discountValue,
        startDateTime,
        endDateTime,
        /* isActive= */ false, // 初期状態は非アクティブ
        ImmutableSet.copyOf(applicableProducts),
        ImmutableList.of(new PromotionCreated(promotionId, name)));
  }

  /** 既存のプロモーションを再構築します（イベントなし） */
  public static Promotion reconstruct(
      PromotionId id,
      String name,
      String description,
      DiscountType discountType,
      BigDecimal discountValue,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      boolean isActive,
      Set<ProductId> applicableProducts) {

    return new Promotion(
        id,
        name,
        description,
        discountType,
        discountValue,
        startDateTime,
        endDateTime,
        isActive,
        ImmutableSet.copyOf(applicableProducts),
        ImmutableList.of());
  }

  /**
   * プロモーションをアクティブにします
   *
   * @param timeProvider 現在時刻プロバイダー
   * @return 更新されたプロモーション
   */
  public Promotion activate(TimeProvider timeProvider) {
    if (this.isActive) {
      return this; // 既にアクティブなら変更なし
    }

    var now = timeProvider.now();
    if (now.isAfter(this.endDateTime)) {
      throw new IllegalStateException("終了日時を過ぎたプロモーションをアクティブにすることはできません");
    }

    return new Promotion(
        this.id,
        this.name,
        this.description,
        this.discountType,
        this.discountValue,
        this.startDateTime,
        this.endDateTime,
        /* isActive= */ true,
        this.applicableProducts,
        ImmutableList.of(new PromotionActivated(this.id, this.name)));
  }

  /**
   * プロモーションを非アクティブにします
   *
   * @return 更新されたプロモーション
   */
  public Promotion deactivate() {
    if (!this.isActive) {
      return this; // 既に非アクティブなら変更なし
    }

    return new Promotion(
        this.id,
        this.name,
        this.description,
        this.discountType,
        this.discountValue,
        this.startDateTime,
        this.endDateTime,
        /* isActive= */ false,
        this.applicableProducts,
        ImmutableList.of(new PromotionDeactivated(this.id, this.name)));
  }

  /**
   * 割引額を計算します
   *
   * @param originalPrice 元の価格
   * @param timeProvider 現在時刻プロバイダー
   * @return 割引額
   */
  public BigDecimal calculateDiscount(BigDecimal originalPrice, TimeProvider timeProvider) {
    if (!isActive) {
      return BigDecimal.ZERO;
    }

    var now = timeProvider.now();
    if (now.isBefore(startDateTime) || now.isAfter(endDateTime)) {
      return BigDecimal.ZERO;
    }

    if (discountType == DiscountType.FIXED_AMOUNT) {
      return discountValue.min(originalPrice); // 元の価格を超える割引はしない
    } else { // PERCENTAGE
      return originalPrice.multiply(discountValue.divide(new BigDecimal("100")));
    }
  }

  /**
   * 指定された商品にこのプロモーションが適用できるかチェックします
   *
   * @param productId 商品ID
   * @return 適用可能な場合true
   */
  public boolean isApplicableTo(ProductId productId) {
    return isActive && (applicableProducts.isEmpty() || applicableProducts.contains(productId));
  }

  /** 割引値のバリデーション */
  private static void validateDiscountValue(DiscountType type, BigDecimal value) {
    if (value.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("割引値は0以上である必要があります");
    }

    if (type == DiscountType.PERCENTAGE && value.compareTo(new BigDecimal("100")) > 0) {
      throw new IllegalArgumentException("割合割引は100%を超えることはできません");
    }
  }

  /** プロモーションが作成されたことを示すドメインイベント */
  public record PromotionCreated(PromotionId promotionId, String name) implements DomainEvent {}

  /** プロモーションがアクティブになったことを示すドメインイベント */
  public record PromotionActivated(PromotionId promotionId, String name) implements DomainEvent {}

  /** プロモーションが非アクティブになったことを示すドメインイベント */
  public record PromotionDeactivated(PromotionId promotionId, String name) implements DomainEvent {}
}
