package com.example.ec_2024b_back.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import java.time.Duration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.ReactiveTransactionManager;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

/** テスト用のMongoDB設定クラス. Testcontainersで起動したMongoDBに対して適切な設定を行います。 */
@TestConfiguration
@EnableReactiveMongoRepositories(basePackages = "com.example.ec_2024b_back") // リポジトリのスキャンパス
public class TestMongoConfig extends AbstractReactiveMongoConfiguration {

  // 共有のMongoDBコンテナを使用（同一JVM内のすべてのテストで再利用）
  private static final MongoDBContainer mongoContainer =
      new MongoDBContainer(DockerImageName.parse("mongo:latest"))
          .withStartupTimeout(Duration.ofMinutes(2));

  // データベース名を指定
  private static final String DATABASE_NAME = "test";

  // スタティックイニシャライザでMongoDBコンテナを起動
  static {
    mongoContainer.start();
  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
  }

  @Override
  protected String getDatabaseName() {
    return DATABASE_NAME;
  }

  @Bean
  @Primary
  @Override
  public MongoClient reactiveMongoClient() {
    return MongoClients.create(mongoContainer.getReplicaSetUrl());
  }

  @Bean
  @Primary
  public ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory() {
    return new SimpleReactiveMongoDatabaseFactory(reactiveMongoClient(), getDatabaseName());
  }

  @Bean
  @Primary
  public ReactiveMongoTemplate reactiveMongoTemplate() {
    return new ReactiveMongoTemplate(reactiveMongoDatabaseFactory());
  }

  /** トランザクションマネージャーを設定します。 トランザクションを使用したテストが必要な場合に有効になります。 */
  @Bean
  ReactiveTransactionManager reactiveTransactionManager(ReactiveMongoDatabaseFactory factory) {
    return new ReactiveMongoTransactionManager(factory);
  }
}
