package com.example.ec_2024b_back.user.interfaces.handler;

import com.example.ec_2024b_back.model.UserRegistrationDto;
import com.example.ec_2024b_back.utils.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

@Testcontainers
@IntegrationTest
public class RegisterUserHandlerIntegrationTest {

  @Container
  static final MongoDBContainer mongoContainer =
      new MongoDBContainer("mongo:5.0.13").withReuse(true);

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
  }

  @Autowired private WebTestClient webTestClient;
  @Autowired private ReactiveMongoTemplate mongoTemplate;

  private static final String TEST_EMAIL = "register-test@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String TEST_FIRST_NAME = "太郎";
  private static final String TEST_LAST_NAME = "山田";
  private static final String TEST_TELEPHONE = "03-1234-5678";

  @BeforeEach
  void setUp() {
    // テスト前にクリーンアップ
    cleanupTestData().block();
  }

  @AfterEach
  void tearDown() {
    // テスト後にクリーンアップ
    cleanupTestData().block();
  }

  private Mono<Void> cleanupTestData() {
    return mongoTemplate
        .remove(Query.query(Criteria.where("email").is(TEST_EMAIL)), "users")
        .then();
  }

  @Test
  void registerUser_shouldReturnCreatedAndUserId_whenRequestIsValid() {
    // テスト用のDTOを作成
    var dto = new UserRegistrationDto();
    dto.setFirstName(TEST_FIRST_NAME);
    dto.setLastName(TEST_LAST_NAME);
    dto.setEmail(TEST_EMAIL);
    dto.setPassword(TEST_PASSWORD);
    dto.setConfirmPassword(TEST_PASSWORD);
    dto.setZipcode("123-4567");
    dto.setPrefecture("TOKYO");
    dto.setMunicipalities("テスト区");
    dto.setAddress("テスト1-2-3");
    dto.setTelephone(TEST_TELEPHONE);

    // APIリクエストを実行
    webTestClient
        .post()
        .uri("/api/registration")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(dto)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.userId")
        .isNotEmpty()
        .jsonPath("$.fullName")
        .isEqualTo(TEST_LAST_NAME + " " + TEST_FIRST_NAME);
  }

  @Test
  void registerUser_shouldReturnBadRequest_whenRequestIsInvalid() {
    // 不正なDTOを作成（必須項目が不足）
    var dto = new UserRegistrationDto();
    dto.setFirstName(TEST_FIRST_NAME);
    // lastName未設定
    dto.setEmail(TEST_EMAIL);
    dto.setPassword(TEST_PASSWORD);
    // 住所関連未設定
    dto.setTelephone(TEST_TELEPHONE);

    // APIリクエストを実行
    webTestClient
        .post()
        .uri("/api/registration")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(dto)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void registerUser_shouldReturnBadRequest_whenPasswordsDoNotMatch() {
    // パスワードと確認用パスワードが一致しないDTO
    var dto = new UserRegistrationDto();
    dto.setFirstName(TEST_FIRST_NAME);
    dto.setLastName(TEST_LAST_NAME);
    dto.setEmail(TEST_EMAIL);
    dto.setPassword(TEST_PASSWORD);
    dto.setConfirmPassword("differentpassword"); // 不一致
    dto.setZipcode("123-4567");
    dto.setPrefecture("TOKYO");
    dto.setMunicipalities("テスト区");
    dto.setAddress("テスト1-2-3");
    dto.setTelephone(TEST_TELEPHONE);

    // APIリクエストを実行
    webTestClient
        .post()
        .uri("/api/registration")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(dto)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void registerUser_shouldReturnConflict_whenEmailAlreadyExists() {
    // テスト用のDTOを作成
    var dto = new UserRegistrationDto();
    dto.setFirstName(TEST_FIRST_NAME);
    dto.setLastName(TEST_LAST_NAME);
    dto.setEmail(TEST_EMAIL);
    dto.setPassword(TEST_PASSWORD);
    dto.setConfirmPassword(TEST_PASSWORD);
    dto.setZipcode("123-4567");
    dto.setPrefecture("TOKYO");
    dto.setMunicipalities("テスト区");
    dto.setAddress("テスト1-2-3");
    dto.setTelephone(TEST_TELEPHONE);

    // 1回目は成功するはず
    webTestClient
        .post()
        .uri("/api/registration")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(dto)
        .exchange()
        .expectStatus()
        .isCreated();

    // 同じメールアドレスで2回目を試みる（競合エラーになるはず）
    webTestClient
        .post()
        .uri("/api/registration")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(dto)
        .exchange()
        .expectStatus()
        .isEqualTo(409) // Conflict
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("EMAIL_ALREADY_EXISTS");
  }
}
