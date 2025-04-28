package com.example.ec_2024b_back.user.application.usecase;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.account.domain.models.Account.AccountId;
import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.user.domain.workflow.RegisterUserWorkflow;
import com.example.ec_2024b_back.user.domain.workflow.RegisterUserWorkflow.EmailAlreadyExistsException;
import com.example.ec_2024b_back.utils.Fast;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Fast
@ExtendWith(MockitoExtension.class)
class RegisterUserUsecaseTest {

  @Mock private RegisterUserWorkflow registerUserWorkflow;

  @InjectMocks private RegisterUserUsecase registerUserUsecase;

  private static final String TEST_EMAIL = "test@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String TEST_FIRST_NAME = "太郎";
  private static final String TEST_LAST_NAME = "山田";
  private static final String TEST_TELEPHONE = "03-1234-5678";
  private static final String TEST_USER_ID = UUID.randomUUID().toString();

  private Address address;
  private User testUser;

  @BeforeEach
  void setUp() {
    address =
        new Address(
            new Address.Zipcode("123-4567"),
            Address.Prefecture.TOKYO,
            new Address.Municipalities("テスト区"),
            new Address.DetailAddress("テスト1-2-3"));

    testUser =
        new User(
            new AccountId(TEST_USER_ID),
            TEST_FIRST_NAME,
            TEST_LAST_NAME,
            address,
            TEST_TELEPHONE,
            "hashedPassword");

    // ワークフローの正常終了をモック
    when(registerUserWorkflow.execute(
            eq(TEST_FIRST_NAME),
            eq(TEST_LAST_NAME),
            eq(TEST_EMAIL),
            eq(TEST_PASSWORD),
            eq(address),
            eq(TEST_TELEPHONE)))
        .thenReturn(Mono.just(testUser));
  }

  @Test
  void execute_shouldReturnSuccessDto_whenRegistrationSucceeds() {
    // 実行
    Mono<RegisterUserUsecase.RegisterUserSuccessDto> result =
        registerUserUsecase.execute(
            TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD, address, TEST_TELEPHONE);

    // 検証
    StepVerifier.create(result)
        .expectNextMatches(
            dto -> {
              // DTOのフィールドが期待通りかチェック
              return Objects.equals(dto.userId(), TEST_USER_ID)
                  && Objects.equals(dto.fullName(), (TEST_LAST_NAME + " " + TEST_FIRST_NAME));
            })
        .verifyComplete();
  }

  @Test
  void execute_shouldFailWithRegistrationFailedException_whenWorkflowFails() {
    // ワークフローがエラーを返すケース
    when(registerUserWorkflow.execute(
            eq(TEST_FIRST_NAME),
            eq(TEST_LAST_NAME),
            eq(TEST_EMAIL),
            eq(TEST_PASSWORD),
            eq(address),
            eq(TEST_TELEPHONE)))
        .thenReturn(Mono.error(new EmailAlreadyExistsException(TEST_EMAIL)));

    // 実行
    Mono<RegisterUserUsecase.RegisterUserSuccessDto> result =
        registerUserUsecase.execute(
            TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD, address, TEST_TELEPHONE);

    // 検証
    StepVerifier.create(result)
        .expectError(RegisterUserUsecase.RegistrationFailedException.class)
        .verify();
  }
}
