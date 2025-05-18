package com.example.ec_2024b_back.sample.infrastructure.repository;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDBにおけるサンプルドキュメント。
 *
 * <p>このクラスは、MongoDBドキュメントのマッピング例を示します。 ドメインモデルとは分離され、インフラストラクチャ層でのみ使用されます。
 */
@Document(collection = "samples")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleDocument {
  @Id private String id;

  private String name;

  private @Nullable String description;

  private String status;

  private Instant createdAt;

  private Instant updatedAt;
}
