package com.example.ec_2024b_back.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

/**
 * テスト用のMongoDB設定クラス. `@DataMongoTest` が使用する組み込みMongoDBの設定をオーバーライドする場合や、 特定の設定を行いたい場合に使用します. (今回は
 * `@DataMongoTest` のデフォルト動作で問題ない可能性もありますが、 タイムアウトエラーが発生したため、明示的な設定を試みます)
 */
@TestConfiguration
@EnableReactiveMongoRepositories(basePackages = "com.example.ec_2024b_back") // リポジトリのスキャンパス
public class TestMongoConfig extends AbstractReactiveMongoConfiguration {

  // 組み込みMongoDBがデフォルトで使用するデータベース名を指定
  private static final String DATABASE_NAME = "test";

  @Override
  protected String getDatabaseName() {
    return DATABASE_NAME;
  }

  // 必要に応じてMongoClientのBeanをカスタマイズできます
  // (今回はデフォルトの組み込みMongoClientを使用)
  // @Bean
  // @Override
  // public MongoClient reactiveMongoClient() {
  //     // return MongoClients.create("mongodb://localhost:27017"); // 例: 外部DBを使う場合
  //     return MongoClients.create(); // デフォルトの組み込み設定
  // }
}
