package com.example.ec_2024b_back.interfaces.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ec_2024b_back.account.application.usecase.LoginUsecase.LoginSuccessDto;
import com.example.ec_2024b_back.model.LoginDto;
import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.user.infrastructure.repository.document.UserDocument;
import com.example.ec_2024b_back.utils.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@IntegrationTest
public class AuthenticationControllerIntegrationTest {

  @Container
  static final MongoDBContainer mongoContainer =
      new MongoDBContainer("mongo:5.0.13").withReuse(true);

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
  }

  @Autowired private WebTestClient webTestClient;

  @Autowired private ReactiveMongoTemplate mongoTemplate;

  @Autowired private PasswordEncoder passwordEncoder;

  private final String testUserId = "auth-test-user";
  private final String testEmail = "auth@example.com";
  private final String testRawPassword = "password123";
  private String testHashedPassword;

  @BeforeEach
  void setUpDatabase() {
    testHashedPassword = passwordEncoder.encode(testRawPassword);

    var zipcode = new Address.Zipcode("100-0002");
    var prefecture = Address.Prefecture.TOKYO;
    var municipalities = new Address.Municipalities("テスト区Auth");
    var detailAddress = new Address.DetailAddress("テストAuth1-1");
    var address = new Address(zipcode, prefecture, municipalities, detailAddress);
    var testUserDoc =
        new UserDocument(
            testUserId, "Auth", "Test", testEmail, testHashedPassword, address, "222-2222-2222");

    mongoTemplate.save(testUserDoc).block();
  }

  @AfterEach
  void tearDownDatabase() {
    mongoTemplate.dropCollection(UserDocument.class).block();
  }

  @Test
  void login_shouldReturnOkAndToken_whenCredentialsAreValid() {
    var loginDto = new LoginDto();
    loginDto.setEmail(testEmail);
    loginDto.setPassword(testRawPassword);

    webTestClient
        .post()
        .uri("/api/authentication/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(loginDto)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(LoginSuccessDto.class)
        .value(
            dto -> {
              assertThat(dto).isNotNull();
              assertThat(dto.token()).isNotNull().isNotEmpty();
            });
  }

  @Test
  void login_shouldReturnUnauthorized_whenPasswordIsInvalid() {
    var loginDto = new LoginDto();
    loginDto.setEmail(testEmail);
    loginDto.setPassword("wrongPassword");

    webTestClient
        .post()
        .uri("/api/authentication/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(loginDto)
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void login_shouldReturnUnauthorized_whenEmailDoesNotExist() {
    var loginDto = new LoginDto();
    loginDto.setEmail("nonexistent@example.com");
    loginDto.setPassword(testRawPassword);

    webTestClient
        .post()
        .uri("/api/authentication/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(loginDto)
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void login_shouldReturnBadRequest_whenRequestBodyIsInvalid() {
    var invalidLoginDto = new LoginDto();
    invalidLoginDto.setPassword(testRawPassword);

    webTestClient
        .post()
        .uri("/api/authentication/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(invalidLoginDto)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }
}
