package com.example.ec_2024b_back.sample.infrastructure.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ec_2024b_back.config.TestMongoConfig;
import com.example.ec_2024b_back.sample.infrastructure.repository.SampleDocumentRepository;
import com.example.ec_2024b_back.utils.IntegrationTest;
import com.example.ec_2024b_back.utils.Slow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * サンプルAPIの統合テスト。
 *
 * <p>実際のHTTPリクエスト/レスポンスを使用してAPIの動作を検証します。 Testcontainersを使用してテスト用のMongoDBコンテナを起動します。
 */
@Testcontainers
@IntegrationTest
@Import(TestMongoConfig.class)
@Slow
class SampleApiIntegrationTest {

  private WebTestClient client;

  @Autowired private SampleDocumentRepository sampleDocumentRepository;

  @BeforeEach
  void setUp(ApplicationContext context) {
    client = WebTestClient.bindToApplicationContext(context).build();

    // テストデータクリア
    sampleDocumentRepository.deleteAll().block();
  }

  @Test
  void shouldCreateSampleSuccessfully() {
    var name = "テストサンプル";
    var description = "これはテスト用のサンプルです";

    var createRequestJson =
        """
        {
            "name": "%s",
            "description": "%s"
        }
        """
            .formatted(name, description);

    var response =
        client
            .post()
            .uri("/api/samples")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(createRequestJson)
            .exchange();

    response
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .exists()
        .jsonPath("$.name")
        .isEqualTo(name);
  }

  @Test
  void shouldReturnBadRequestWhenNameIsEmpty() {
    var createRequestJson =
        """
        {
            "name": "",
            "description": "説明"
        }
        """;

    var response =
        client
            .post()
            .uri("/api/samples")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(createRequestJson)
            .exchange();

    response
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.message")
        .exists()
        .jsonPath("$.message")
        .value(message -> assertThat(message.toString()).contains("名前は必須です"));
  }

  @Test
  void shouldReturnBadRequestWhenNameIsTooLong() {
    var longName = "a".repeat(101); // 101文字
    var createRequestJson =
        """
        {
            "name": "%s",
            "description": "説明"
        }
        """
            .formatted(longName);

    var response =
        client
            .post()
            .uri("/api/samples")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(createRequestJson)
            .exchange();

    response
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.message")
        .exists()
        .jsonPath("$.message")
        .value(message -> assertThat(message.toString()).contains("名前は100文字以内で入力してください"));
  }

  @Test
  void shouldReturnBadRequestWhenDescriptionIsTooLong() {
    var longDescription = "a".repeat(501); // 501文字
    var createRequestJson =
        """
        {
            "name": "テストサンプル",
            "description": "%s"
        }
        """
            .formatted(longDescription);

    var response =
        client
            .post()
            .uri("/api/samples")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(createRequestJson)
            .exchange();

    response
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.message")
        .exists()
        .jsonPath("$.message")
        .value(message -> assertThat(message.toString()).contains("説明は500文字以内で入力してください"));
  }

  @Test
  void shouldCreateSampleWithoutDescription() {
    var name = "説明なしサンプル";
    var createRequestJson =
        """
        {
            "name": "%s"
        }
        """
            .formatted(name);

    var response =
        client
            .post()
            .uri("/api/samples")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(createRequestJson)
            .exchange();

    response
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .exists()
        .jsonPath("$.name")
        .isEqualTo(name);
  }

  @Test
  void shouldReturnBadRequestWhenNameContainsInvalidCharacters() {
    var createRequestJson =
        """
        {
            "name": "テスト<script>alert('XSS')</script>",
            "description": "説明"
        }
        """;

    var response =
        client
            .post()
            .uri("/api/samples")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(createRequestJson)
            .exchange();

    response
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.message")
        .exists()
        .jsonPath("$.message")
        .value(message -> assertThat(message.toString()).contains("名前に使用できない文字が含まれています"));
  }
}
