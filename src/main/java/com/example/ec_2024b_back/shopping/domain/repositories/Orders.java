package com.example.ec_2024b_back.shopping.domain.repositories;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.shopping.OrderId;
import com.example.ec_2024b_back.shopping.domain.models.Order;
import com.example.ec_2024b_back.shopping.domain.models.OrderStatus;
import org.jmolecules.ddd.types.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** 注文リポジトリのインターフェース */
public interface Orders extends Repository<Order, OrderId> {

  /**
   * 注文IDにより注文を検索します
   *
   * @param id 注文ID
   * @return 検索結果
   */
  Mono<Order> findById(OrderId id);

  /**
   * アカウントIDに紐づく注文をすべて検索します
   *
   * @param accountId アカウントID
   * @return 注文のストリーム
   */
  Flux<Order> findByAccountId(AccountId accountId);

  /**
   * アカウントIDと注文ステータスに紐づく注文をすべて検索します
   *
   * @param accountId アカウントID
   * @param status 注文ステータス
   * @return 注文のストリーム
   */
  Flux<Order> findByAccountIdAndStatus(AccountId accountId, OrderStatus status);

  /**
   * 注文ステータスに紐づく注文をすべて検索します
   *
   * @param status 注文ステータス
   * @return 注文のストリーム
   */
  Flux<Order> findByStatus(OrderStatus status);

  /**
   * 注文を保存します
   *
   * @param order 保存する注文
   * @return 保存された注文
   */
  Mono<Order> save(Order order);
}
