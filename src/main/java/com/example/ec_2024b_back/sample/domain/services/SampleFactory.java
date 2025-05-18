package com.example.ec_2024b_back.sample.domain.services;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.sample.domain.models.Sample;
import com.example.ec_2024b_back.sample.domain.models.SampleStatus;
import com.example.ec_2024b_back.share.domain.models.AuditInfo;
import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.share.domain.services.TimeProvider;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * サンプルエンティティを生成するファクトリー。
 *
 * <p>このクラスは、ファクトリーパターンの実装例を示します。 エンティティの生成ロジックを集約し、依存関係の注入を活用します。
 */
@Component
@RequiredArgsConstructor
public class SampleFactory {
  private final IdGenerator idGenerator;
  private final TimeProvider timeProvider;

  /**
   * 新しいサンプルエンティティを作成します。
   *
   * @param name 名前
   * @param description 説明（nullable）
   * @return 作成されたサンプル
   */
  public Sample create(String name, @Nullable String description) {
    var now = timeProvider.now().atZone(ZoneId.systemDefault()).toInstant();
    var auditInfo = new AuditInfo(now, now);

    return new Sample(
        new SampleId(idGenerator.newId()), name, description, SampleStatus.DRAFT, auditInfo);
  }

  /**
   * サンプルエンティティを復元します。
   *
   * @param id ID
   * @param name 名前
   * @param description 説明（nullable）
   * @param status ステータス
   * @param auditInfo 監査情報
   * @return 復元されたサンプル
   */
  public Sample restore(
      SampleId id,
      String name,
      @Nullable String description,
      SampleStatus status,
      AuditInfo auditInfo) {
    return new Sample(id, name, description, status, auditInfo);
  }
}
