package com.example.ec_2024b_back.sample.infrastructure.repository;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.sample.domain.models.Sample;
import com.example.ec_2024b_back.sample.domain.repositories.Samples;
import com.example.ec_2024b_back.sample.domain.services.SampleFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * サンプルリポジトリのMongoDB実装。
 *
 * <p>このクラスは、リポジトリパターンの実装例を示します。 ドメインモデルとドキュメントモデル間の変換を行います。
 */
@Repository
@RequiredArgsConstructor
public class MongoSamples implements Samples {
  private final SampleDocumentRepository repository;
  private final SampleFactory sampleFactory;

  @Override
  public Mono<Sample> findById(SampleId id) {
    return repository.findById(id.toString()).map(this::toDomain);
  }

  @Override
  public Flux<Sample> findByName(String name) {
    return repository.findByName(name).map(this::toDomain);
  }

  @Override
  public Mono<Sample> save(Sample sample) {
    var document = SampleDocument.fromDomain(sample);
    return repository.save(document).map(this::toDomain);
  }

  @Override
  public Mono<Void> deleteById(SampleId id) {
    return repository.deleteById(id.toString());
  }

  /** ドキュメントをドメインモデルに変換します。 */
  private Sample toDomain(SampleDocument document) {
    return document.toDomain(sampleFactory);
  }
}
