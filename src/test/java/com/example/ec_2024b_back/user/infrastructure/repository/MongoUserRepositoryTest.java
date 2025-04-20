package com.example.ec_2024b_back.user.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.user.infrastructure.repository.document.UserDocument;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

@DataMongoTest
@Testcontainers
class MongoUserRepositoryTest {

  @Container static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0.13");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Autowired private ReactiveMongoTemplate mongoTemplate;

  @Autowired private MongoUserRepository userRepository;

  private UserDocument testUserDoc;
  private static final String TEST_EMAIL = "mongo@example.com";
  private static final String NON_EXISTENT_EMAIL = "notfound@example.com";

  @BeforeEach
  void setUpDatabase() {
    var zipcode = new Address.Zipcode("100-0001");
    var prefecture = Address.Prefecture.TOKYO;
    var municipalities = new Address.Municipalities("テスト区Mongo");
    var detailAddress = new Address.DetailAddress("テストMongo1-1");
    var address = new Address(zipcode, prefecture, municipalities, detailAddress);
    testUserDoc =
        new UserDocument(
            "mongo-user-id",
            "Mongo",
            "Test",
            TEST_EMAIL,
            "hashedMongoPass",
            address,
            "111-1111-1111");

    mongoTemplate.save(testUserDoc).block();
  }

  @AfterEach
  void tearDownDatabase() {
    mongoTemplate.dropCollection(UserDocument.class).block();
  }

  @Test
  void findDocumentByEmail_shouldReturnUserDocument_whenEmailExists() {
    var resultMono = userRepository.findDocumentByEmail(TEST_EMAIL);

    StepVerifier.create(resultMono)
        .assertNext(
            doc -> {
              assertThat(doc).isNotNull();
              assertThat(doc.getId()).isEqualTo(testUserDoc.getId());
              assertThat(doc.getEmail()).isEqualTo(TEST_EMAIL);
              assertThat(doc.getFirstName()).isEqualTo(testUserDoc.getFirstName());
            })
        .verifyComplete();
  }

  @Test
  void findDocumentByEmail_shouldReturnEmptyMono_whenEmailDoesNotExist() {
    var resultMono = userRepository.findDocumentByEmail(NON_EXISTENT_EMAIL);

    StepVerifier.create(resultMono).verifyComplete();
  }

  @Test
  void findByEmail_shouldReturnSuccessWithUserOption_whenEmailExists() {
    var resultMono = userRepository.findByEmail(TEST_EMAIL);

    StepVerifier.create(resultMono)
        .assertNext(
            tryResult -> {
              assertThat(tryResult).isPresent();
              var userOption = tryResult.get();
              assertThat(userOption).isNotNull();
              var user = userOption;
              assertThat(user.id().id()).isEqualTo(testUserDoc.getId());
              assertThat(user.firstName()).isEqualTo(testUserDoc.getFirstName());
            })
        .verifyComplete();
  }

  @Test
  void findByEmail_shouldReturnSuccessWithEmptyOption_whenEmailDoesNotExist() {
    var resultMono = userRepository.findByEmail(NON_EXISTENT_EMAIL);

    StepVerifier.create(resultMono)
        .assertNext(
            userOption -> {
              // Optionalは存在するが、中身は空
              assertThat(userOption).isEqualTo(Optional.empty());
            })
        .verifyComplete();
  }
}
