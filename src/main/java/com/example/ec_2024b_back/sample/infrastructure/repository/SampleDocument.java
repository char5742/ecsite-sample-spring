package com.example.ec_2024b_back.sample.infrastructure.repository;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.sample.domain.models.Sample;
import com.example.ec_2024b_back.sample.domain.models.SampleStatus;
import com.example.ec_2024b_back.sample.domain.services.SampleFactory;
import com.example.ec_2024b_back.share.domain.models.AuditInfo;
import java.time.Instant;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDBにおけるサンプルドキュメント。
 *
 * <p>このクラスは、MongoDBドキュメントのマッピング例を示します。 ドメインモデルとは分離され、インフラストラクチャ層でのみ使用されます。
 */
@Document(collection = "samples")
public record SampleDocument(
    @Id String id,
    String name,
    @Nullable String description,
    String status,
    Instant createdAt,
    Instant updatedAt) {

  /** SpringData用のNo-argコンストラクタ */
  public SampleDocument() {
    this("", "", null, SampleStatus.DRAFT.name(), Instant.now(), Instant.now());
  }

  /**
   * ドメインモデルからドキュメントに変換します。
   *
   * @param sample サンプルドメインモデル
   * @return 作成されたドキュメント
   */
  public static SampleDocument fromDomain(Sample sample) {
    return new SampleDocument(
        sample.getId().toString(),
        sample.getName(),
        sample.getDescription(),
        sample.getStatus().name(),
        sample.getAuditInfo().createdAt(),
        sample.getAuditInfo().updatedAt());
  }

  /**
   * ドキュメントからドメインモデルに変換します。
   *
   * @return 作成されたドメインモデル
   */
  public Sample toDomain(SampleFactory factory) {
    return factory.restore(
        new SampleId(UUID.fromString(id)),
        name,
        description,
        SampleStatus.valueOf(status),
        new AuditInfo(createdAt, updatedAt));
  }
}
