package com.example.ec_2024b_back.account.domain.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.account.domain.models.Account;
import com.example.ec_2024b_back.account.domain.step.FindUserByEmailStep;
import com.example.ec_2024b_back.account.domain.step.GenerateJwtTokenStep;
import com.example.ec_2024b_back.account.domain.step.VerifyPasswordStep;
import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.share.domain.models.Address.Zipcode;
import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.user.infrastructure.repository.MongoUserRepository;
import com.example.ec_2024b_back.user.infrastructure.repository.document.UserDocument;
import com.example.ec_2024b_back.utils.Fast;
import io.vavr.Tuple;
import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@Fast
class LoginWorkflowTest {

  @Mock private FindUserByEmailStep findUserByEmailStep;
  @Mock private VerifyPasswordStep verifyPasswordStep;
  @Mock private GenerateJwtTokenStep generateJwtTokenStep;
  @Mock private MongoUserRepository userRepository;

  @InjectMocks private LoginWorkflow loginWorkflow;

  private final String testEmail = "test@example.com";
  private final String rawPassword = "rawPassword123";
  private final String hashedPassword = "hashedPasswordXYZ";
  private final String accountId = "test-id";
  private final String expectedToken = "generated.jwt.token";
  private final UserDocument testUserDoc =
      new UserDocument(
          accountId,
          "Taro",
          "Yamada",
          testEmail,
          hashedPassword,
          new Address(
              new Zipcode("100-0000"),
              Address.Prefecture.TOKYO,
              new Address.Municipalities("Chiyoda"),
              new Address.DetailAddress("1-1-1")),
          "090-1234-5678");
  private final User testUser =
      new User(
          new Account.AccountId(accountId),
          "Taro",
          "Yamada",
          new Address(
              new Zipcode("100-0000"),
              Address.Prefecture.TOKYO,
              new Address.Municipalities("Chiyoda"),
              new Address.DetailAddress("1-1-1")),
          "090-1234-5678");

  @BeforeEach
  void setUp() {}

  @Test
  void execute_shouldReturnSuccessWithToken_whenAllStepsSucceed() {
    // Arrange
    when(userRepository.findDocumentByEmail(testEmail)).thenReturn(Mono.just(testUserDoc));
    when(verifyPasswordStep.apply(Tuple.of(accountId, hashedPassword, rawPassword)))
        .thenReturn(Try.success(accountId));
    when(generateJwtTokenStep.apply(any(User.class))).thenReturn(Try.success(expectedToken));

    // Act
    var resultMono = loginWorkflow.execute(testEmail, rawPassword);

    // Assert
    StepVerifier.create(resultMono)
        .assertNext(
            resultTry -> {
              assertThat(resultTry.isSuccess()).isTrue();
              assertThat(resultTry.get()).isEqualTo(expectedToken);
            })
        .verifyComplete();
  }

  @Test
  void execute_shouldReturnFailure_whenUserNotFound() {
    // Arrange
    when(userRepository.findDocumentByEmail(testEmail)).thenReturn(Mono.empty());

    // Act
    var resultMono = loginWorkflow.execute(testEmail, rawPassword);

    // Assert
    StepVerifier.create(resultMono)
        .assertNext(
            resultTry -> {
              assertThat(resultTry.isFailure()).isTrue();
              assertThat(resultTry.getCause())
                  .isInstanceOf(LoginWorkflow.UserNotFoundException.class);
            })
        .verifyComplete();
  }

  @Test
  void execute_shouldReturnFailure_whenPasswordVerificationFails() {
    // Arrange
    when(userRepository.findDocumentByEmail(testEmail)).thenReturn(Mono.just(testUserDoc));
    var passwordException = new VerifyPasswordStep.InvalidPasswordException();
    when(verifyPasswordStep.apply(Tuple.of(accountId, hashedPassword, rawPassword)))
        .thenReturn(Try.failure(passwordException));

    // Act
    var resultMono = loginWorkflow.execute(testEmail, rawPassword);

    // Assert
    StepVerifier.create(resultMono)
        .assertNext(
            resultTry -> {
              assertThat(resultTry.isFailure()).isTrue();
              assertThat(resultTry.getCause()).isEqualTo(passwordException);
            })
        .verifyComplete();
  }

  @Test
  void execute_shouldReturnFailure_whenTokenGenerationFails() {
    // Arrange
    when(userRepository.findDocumentByEmail(testEmail)).thenReturn(Mono.just(testUserDoc));
    when(verifyPasswordStep.apply(Tuple.of(accountId, hashedPassword, rawPassword)))
        .thenReturn(Try.success(accountId));
    var tokenException = new RuntimeException("Token generation error");
    when(generateJwtTokenStep.apply(any(User.class))).thenReturn(Try.failure(tokenException));

    // Act
    var resultMono = loginWorkflow.execute(testEmail, rawPassword);

    // Assert
    StepVerifier.create(resultMono)
        .assertNext(
            resultTry -> {
              assertThat(resultTry.isFailure()).isTrue();
              assertThat(resultTry.getCause()).isEqualTo(tokenException);
            })
        .verifyComplete();
  }
}
