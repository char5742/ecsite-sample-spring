package com.example.ec_2024b_back.product.application.workflow;

import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.product.domain.models.Promotion;
import com.example.ec_2024b_back.product.domain.models.Promotion.DiscountType;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.google.common.collect.ImmutableSet;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** プロモーション作成ワークフロー */
public interface CreatePromotionWorkflow {

  /** 対象商品の存在を検証する */
  @FunctionalInterface
  interface ValidateProductsStep extends Function<Context.Input, Mono<Context.Validated>> {}

  /** プロモーションを作成する */
  @FunctionalInterface
  interface CreatePromotionStep extends Function<Context.Validated, Mono<Context.Created>> {}

  sealed interface Context permits Context.Input, Context.Validated, Context.Created {

    record Input(
        String name,
        String description,
        DiscountType discountType,
        BigDecimal discountValue,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        ImmutableSet<ProductId> applicableProducts)
        implements Context {}

    record Validated(
        String name,
        String description,
        DiscountType discountType,
        BigDecimal discountValue,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        ImmutableSet<ProductId> applicableProducts)
        implements Context {}

    record Created(Promotion promotion) implements Context {}
  }

  /**
   * プロモーション作成処理を実行します
   *
   * @param name 名前
   * @param description 説明
   * @param discountType 割引タイプ
   * @param discountValue 割引値
   * @param startDateTime 開始日時
   * @param endDateTime 終了日時
   * @param applicableProducts 適用対象商品
   * @return 作成されたプロモーションを含むMono
   */
  Mono<Context.Created> execute(
      String name,
      String description,
      DiscountType discountType,
      BigDecimal discountValue,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Set<ProductId> applicableProducts);

  /** 商品が存在しない場合のカスタム例外 */
  class ProductNotFoundException extends DomainException {
    public ProductNotFoundException(ProductId productId) {
      super("商品ID: " + productId.value() + " は存在しません");
    }
  }

  /** 無効な割引値のカスタム例外 */
  class InvalidDiscountValueException extends DomainException {
    public InvalidDiscountValueException(String message) {
      super(message);
    }
  }
}
