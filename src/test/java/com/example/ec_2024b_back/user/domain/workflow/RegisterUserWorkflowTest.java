package com.example.ec_2024b_back.user.domain.workflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.auth.domain.models.Account.AccountId;
import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.user.domain.repository.UserRepository;
import com.example.ec_2024b_back.user.domain.step.CheckEmailUniquenessStep;
import com.example.ec_2024b_back.user.domain.step.HashPasswordStep;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Fast
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class RegisterUserWorkflowTest {

  @Mock private UserRepository userRepository;
  @Mock private CheckEmailUniquenessStep checkEmailUniquenessStep;
  @Mock private HashPasswordStep hashPasswordStep;

  @InjectMocks private RegisterUserWorkflow registerUserWorkflow;

  private static final String TEST_EMAIL = "test@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String TEST_FIRST_NAME = "太郎";
  private static final String TEST_LAST_NAME = "山田";
  private static final String TEST_TELEPHONE = "03-1234-5678";
  private static final String HASHED_PASSWORD = "$2a$10$abcdefghijklmnopqrstuvwxyz123456789";

  private Address address;

  @BeforeEach
  void setUp() {
    address =
        new Address(
            new Address.Zipcode("123-4567"),
            Address.Prefecture.TOKYO,
            new Address.Municipalities("テスト区"),
            new Address.DetailAddress("テスト1-2-3"));

    // メールアドレス一意性チェックのモック
    when(checkEmailUniquenessStep.apply(TEST_EMAIL)).thenReturn(Mono.just(TEST_EMAIL));

    // パスワードハッシュ化のモック
    when(hashPasswordStep.apply(TEST_PASSWORD)).thenReturn(Mono.just(HASHED_PASSWORD));

    // ユーザー保存のモック
    when(userRepository.saveWithEmail(any(User.class), eq(TEST_EMAIL)))
        .thenAnswer(
            invocation -> {
              User user = invocation.getArgument(0);
              // 保存時にIDが付与されるシミュレーション
              var newId = new AccountId(UUID.randomUUID().toString());
              var savedUser =
                  new User(
                      newId,
                      user.firstName(),
                      user.lastName(),
                      user.address(),
                      user.telephone(),
                      user.password());
              return Mono.just(savedUser);
            });
  }

  @Test
  void execute_shouldCreateUser_whenAllInputsAreValid() {
    // 実行
    Mono<User> result =
        registerUserWorkflow.execute(
            TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD, address, TEST_TELEPHONE);

    // 検証
    StepVerifier.create(result)
        .expectNextMatches(
            user -> {
              // 各フィールドが期待通りかチェック
              return user.id() != null
                  && Objects.equals(user.firstName(), TEST_FIRST_NAME)
                  && Objects.equals(user.lastName(), TEST_LAST_NAME)
                  && address.equals(user.address())
                  && Objects.equals(user.telephone(), TEST_TELEPHONE)
                  && Objects.equals(user.password(), HASHED_PASSWORD);
            })
        .verifyComplete();
  }

  @Test
  void execute_shouldFailWithEmailAlreadyExistsException_whenEmailIsTaken() {
    // メールアドレスの一意性チェックがエラーを返すようモックを変更
    when(checkEmailUniquenessStep.apply(TEST_EMAIL))
        .thenReturn(Mono.error(new EmailAlreadyExistsException(TEST_EMAIL)));

    // 実行
    Mono<User> result =
        registerUserWorkflow.execute(
            TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD, address, TEST_TELEPHONE);

    // 検証
    StepVerifier.create(result).expectError(EmailAlreadyExistsException.class).verify();
  }
}
