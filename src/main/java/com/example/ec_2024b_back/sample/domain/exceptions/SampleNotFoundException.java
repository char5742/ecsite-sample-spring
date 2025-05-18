package com.example.ec_2024b_back.sample.domain.exceptions;

import com.example.ec_2024b_back.sample.SampleId;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;

/**
 * サンプルが見つからない場合の例外。
 *
 * <p>このクラスは、ドメイン例外の実装例を示します。 日本語のエラーメッセージを含み、ビジネスロジックでの使用を想定しています。
 */
public class SampleNotFoundException extends DomainException {
  private final SampleId sampleId;

  /**
   * サンプルが見つからない例外を作成します。
   *
   * @param sampleId 見つからなかったサンプルのID
   */
  public SampleNotFoundException(SampleId sampleId) {
    super("Sample not found: " + sampleId.value());
    this.sampleId = sampleId;
  }

  /**
   * 日本語のエラーメッセージを返します。
   *
   * @return 日本語のエラーメッセージ
   */
  @Override
  public String getMessage() {
    return "サンプルが見つかりません。ID: " + sampleId.value();
  }

  /**
   * 見つからなかったサンプルのIDを取得します。
   *
   * @return サンプルID
   */
  public SampleId getSampleId() {
    return sampleId;
  }
}
