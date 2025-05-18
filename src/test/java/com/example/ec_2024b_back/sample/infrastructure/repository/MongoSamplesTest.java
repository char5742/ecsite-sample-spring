package com.example.ec_2024b_back.sample.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ec_2024b_back.config.TestMongoConfig;
import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.sample.domain.models.Sample;
import com.example.ec_2024b_back.sample.domain.models.SampleStatus;
import com.example.ec_2024b_back.share.domain.models.AuditInfo;
import com.example.ec_2024b_back.utils.IntegrationTest;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

/**
 * MongoSamplesリポジトリの統合テスト。
 *
 * <p>実際のMongoDBを使用してリポジトリの動作を検証します。 Testcontainersを使用してテスト用のMongoDBコンテナを起動します。
 */
@Testcontainers
@IntegrationTest
@Import(TestMongoConfig.class)
class MongoSamplesTest {

  @Autowired private MongoSamples mongoSamples;

  @Autowired private SampleDocumentRepository sampleDocumentRepository;

  private Sample sampleEntity;

  @BeforeEach
  void setUp() {
    // テストデータクリア
    sampleDocumentRepository.deleteAll().block();

    // ファクトリーの初期化

    // テスト用サンプルエンティティの作成
    var id = new SampleId(UUID.randomUUID());
    var now = Instant.now();
    var auditInfo = new AuditInfo(now, now);
    sampleEntity = new Sample(id, "テストサンプル", "これはテスト用のサンプルです", SampleStatus.ACTIVE, auditInfo);
  }

  @Test
  void shouldSaveAndFindById() {
    // save
    StepVerifier.create(mongoSamples.save(sampleEntity))
        .assertNext(
            saved -> {
              assertThat(saved.getId()).isEqualTo(sampleEntity.getId());
              assertThat(saved.getName()).isEqualTo(sampleEntity.getName());
              assertThat(saved.getDescription()).isEqualTo(sampleEntity.getDescription());
              assertThat(saved.getStatus()).isEqualTo(sampleEntity.getStatus());
            })
        .verifyComplete();

    // findById
    StepVerifier.create(mongoSamples.findById(sampleEntity.getId()))
        .assertNext(
            found -> {
              assertThat(found.getId()).isEqualTo(sampleEntity.getId());
              assertThat(found.getName()).isEqualTo(sampleEntity.getName());
              assertThat(found.getDescription()).isEqualTo(sampleEntity.getDescription());
              assertThat(found.getStatus()).isEqualTo(sampleEntity.getStatus());
            })
        .verifyComplete();
  }

  @Test
  void shouldReturnEmptyWhenNotFound() {
    var nonExistentId = new SampleId(UUID.randomUUID());

    StepVerifier.create(mongoSamples.findById(nonExistentId)).verifyComplete();
  }

  @Test
  void shouldFindByName() {
    // 複数のサンプルを保存
    var sample1 = createSampleWithName("テストサンプル1");
    var sample2 = createSampleWithName("テストサンプル2");
    var sample3 = createSampleWithName("テストサンプル1"); // 同じ名前

    mongoSamples.save(sample1).block();
    mongoSamples.save(sample2).block();
    mongoSamples.save(sample3).block();

    // 名前で検索
    StepVerifier.create(mongoSamples.findByName("テストサンプル1"))
        .assertNext(found -> assertThat(found.getId()).isIn(sample1.getId(), sample3.getId()))
        .assertNext(found -> assertThat(found.getId()).isIn(sample1.getId(), sample3.getId()))
        .verifyComplete();
  }

  @Test
  void shouldDeleteById() {
    // サンプルを保存
    mongoSamples.save(sampleEntity).block();

    // 削除前に存在確認
    StepVerifier.create(mongoSamples.findById(sampleEntity.getId()))
        .assertNext(found -> assertThat(found).isNotNull())
        .verifyComplete();

    // 削除
    StepVerifier.create(mongoSamples.deleteById(sampleEntity.getId())).verifyComplete();

    // 削除後に存在しないことを確認
    StepVerifier.create(mongoSamples.findById(sampleEntity.getId())).verifyComplete();
  }

  private static Sample createSampleWithName(String name) {
    var id = new SampleId(UUID.randomUUID());
    var now = Instant.now();
    var auditInfo = new AuditInfo(now, now);
    return new Sample(id, name, null, SampleStatus.ACTIVE, auditInfo);
  }
}
