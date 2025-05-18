package com.example.ec_2024b_back.sample.domain.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.share.domain.models.AuditInfo;
import com.example.ec_2024b_back.utils.Fast;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * サンプルエンティティのテスト。
 *
 * <p>このクラスは、ドメインモデルの単体テスト例を示します。 値オブジェクトの不変性、ビジネスルールの検証などをテストします。
 */
@Fast
class SampleTest {

  private SampleId id;
  private AuditInfo auditInfo;

  @BeforeEach
  void setUp() {
    id = new SampleId(UUID.randomUUID());
    var now = Instant.now();
    auditInfo = new AuditInfo(now, now);
  }

  @Test
  void shouldCreateSample_whenValidParametersProvided() {
    var name = "テストサンプル";
    var description = "これはテスト用のサンプルです";
    var status = SampleStatus.DRAFT;

    var sample = new Sample(id, name, description, status, auditInfo);

    assertThat(sample.getId()).isEqualTo(id);
    assertThat(sample.getName()).isEqualTo(name);
    assertThat(sample.getDescription()).isEqualTo(description);
    assertThat(sample.getStatus()).isEqualTo(status);
    assertThat(sample.getAuditInfo()).isEqualTo(auditInfo);
  }

  @Test
  void shouldThrowException_whenIdIsNull() {
    assertThatThrownBy(() -> new Sample(null, "名前", null, SampleStatus.DRAFT, auditInfo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("IDは必須です");
  }

  @Test
  void shouldThrowException_whenNameIsNull() {
    assertThatThrownBy(() -> new Sample(id, null, null, SampleStatus.DRAFT, auditInfo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("名前は必須です");
  }

  @Test
  void shouldThrowException_whenNameIsBlank() {
    assertThatThrownBy(() -> new Sample(id, "  ", null, SampleStatus.DRAFT, auditInfo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("名前は必須です");
  }

  @Test
  void shouldUpdateName_whenValidNameProvided() {
    var sample = new Sample(id, "元の名前", null, SampleStatus.DRAFT, auditInfo);
    var newName = "新しい名前";

    sample.updateName(newName);

    assertThat(sample.getName()).isEqualTo(newName);
  }

  @Test
  void shouldThrowException_whenUpdatingWithNullName() {
    var sample = new Sample(id, "元の名前", null, SampleStatus.DRAFT, auditInfo);

    assertThatThrownBy(() -> sample.updateName(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("名前は必須です");
  }

  @Test
  void shouldUpdateDescription_whenValidDescriptionProvided() {
    var sample = new Sample(id, "名前", "元の説明", SampleStatus.DRAFT, auditInfo);
    var newDescription = "新しい説明";

    sample.updateDescription(newDescription);

    assertThat(sample.getDescription()).isEqualTo(newDescription);
  }

  @Test
  void shouldAllowNullDescription() {
    var sample = new Sample(id, "名前", "説明あり", SampleStatus.DRAFT, auditInfo);

    sample.updateDescription(null);

    assertThat(sample.getDescription()).isNull();
  }

  @Test
  void shouldUpdateStatus_whenValidStatusProvided() {
    var sample = new Sample(id, "名前", null, SampleStatus.DRAFT, auditInfo);

    sample.updateStatus(SampleStatus.ACTIVE);

    assertThat(sample.getStatus()).isEqualTo(SampleStatus.ACTIVE);
  }

  @Test
  void shouldThrowException_whenUpdatingWithNullStatus() {
    var sample = new Sample(id, "名前", null, SampleStatus.DRAFT, auditInfo);

    assertThatThrownBy(() -> sample.updateStatus(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ステータスは必須です");
  }

  @Test
  void shouldReturnTrue_whenStatusIsActive() {
    var sample = new Sample(id, "名前", null, SampleStatus.ACTIVE, auditInfo);

    assertThat(sample.isActive()).isTrue();
  }

  @Test
  void shouldReturnFalse_whenStatusIsNotActive() {
    var sample = new Sample(id, "名前", null, SampleStatus.DRAFT, auditInfo);

    assertThat(sample.isActive()).isFalse();
  }
}
