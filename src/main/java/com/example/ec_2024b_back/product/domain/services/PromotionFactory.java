package com.example.ec_2024b_back.product.domain.services;

import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.product.PromotionId;
import com.example.ec_2024b_back.product.domain.models.Promotion;
import com.example.ec_2024b_back.product.domain.models.Promotion.DiscountType;
import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** プロモーションを作成するファクトリー */
@Component
@RequiredArgsConstructor
public class PromotionFactory {
  private final IdGenerator idGen;

  /**
   * 新しいプロモーションを作成します
   *
   * @param name プロモーション名
   * @param description プロモーションの説明
   * @param discountType 割引タイプ
   * @param discountValue 割引値
   * @param startDateTime 開始日時
   * @param endDateTime 終了日時
   * @param applicableProducts 適用対象商品ID一覧
   * @return 作成されたプロモーション
   */
  public Promotion create(
      String name,
      String description,
      DiscountType discountType,
      BigDecimal discountValue,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Set<ProductId> applicableProducts) {
    return Promotion.create(
        new PromotionId(idGen.newId()),
        name,
        description,
        discountType,
        discountValue,
        startDateTime,
        endDateTime,
        applicableProducts);
  }
}
