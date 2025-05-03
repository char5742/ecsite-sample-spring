package com.example.ec_2024b_back.product.domain.repositories;

import com.example.ec_2024b_back.product.ProductId;
import com.example.ec_2024b_back.product.PromotionId;
import com.example.ec_2024b_back.product.domain.models.Promotion;
import java.time.LocalDateTime;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** プロモーションリポジトリインターフェース */
public interface Promotions {

  /**
   * IDによってプロモーションを検索します
   *
   * @param id プロモーションID
   * @return プロモーションを含むMono、見つからない場合は empty
   */
  Mono<Promotion> findById(PromotionId id);

  /**
   * アクティブなプロモーションを検索します
   *
   * @return アクティブなプロモーションのFlux
   */
  Flux<Promotion> findActive();

  /**
   * 特定の商品に適用可能なアクティブなプロモーションを検索します
   *
   * @param productId 商品ID
   * @return プロモーションのFlux
   */
  Flux<Promotion> findActiveByProductId(ProductId productId);

  /**
   * 期間内のプロモーションを検索します
   *
   * @param dateTime 検索する日時
   * @return プロモーションのFlux
   */
  Flux<Promotion> findByDateTime(LocalDateTime dateTime);

  /**
   * プロモーションを保存します
   *
   * @param promotion 保存するプロモーション
   * @return 保存されたプロモーションを含むMono
   */
  Mono<Promotion> save(Promotion promotion);
}
