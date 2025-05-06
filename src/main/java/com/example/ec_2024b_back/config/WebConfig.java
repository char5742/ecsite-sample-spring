package com.example.ec_2024b_back.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Web関連の設定クラス. エラーハンドリングに必要な設定を提供します。 */
@Configuration
public class WebConfig {

  /**
   * WebPropertiesを提供. エラーハンドラーで使用されるリソース設定です。
   *
   * @return WebProperties インスタンス
   */
  @Bean
  public WebProperties webProperties() {
    return new WebProperties();
  }

  /**
   * WebProperties.Resourcesを提供. エラーハンドラーで使用されるリソース設定です。
   *
   * @return WebProperties.Resources インスタンス
   */
  @Bean
  public WebProperties.Resources resources() {
    return new WebProperties().getResources();
  }
}
