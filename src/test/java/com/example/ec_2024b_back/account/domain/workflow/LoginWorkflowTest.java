package com.example.ec_2024b_back.account.domain.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.account.domain.models.Account;
import com.example.ec_2024b_back.account.domain.step.GenerateJwtTokenStep;
import com.example.ec_2024b_back.account.domain.step.VerifyPasswordStep;
import com.example.ec_2024b_back.account.domain.step.VerifyPasswordStep.InvalidPasswordException;
import com.example.ec_2024b_back.account.domain.workflow.LoginWorkflow.UserNotFoundException;
import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.user.infrastructure.repository.MongoUserRepository;
import com.example.ec_2024b_back.user.infrastructure.repository.document.UserDocument;
import com.example.ec_2024b_back.utils.Fast;
import io.vavr.Tuple3;
import io.vavr.control.Try;
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
class LoginWorkflowTest {

  @Mock private VerifyPasswordStep verifyPasswordStep;
  @Mock private GenerateJwtTokenStep generateJwtTokenStep;
  @Mock private MongoUserRepository userRepository;

  @InjectMocks private LoginWorkflow loginWorkflow;

  private UserDocument userDocument;
  private User user;
  private String email = "test@example.com";
  private String rawPassword = "password";
  private String hashedPassword = "hashedPassword";
  private String accountId = "user-id-123";
  private String token = "dummy-jwt-token";

  @BeforeEach
  void setUp() {
    var zipcode = new Address.Zipcode("100-0000");
    var prefecture = Address.Prefecture.TOKYO;
    var municipalities = new Address.Municipalities("千代田区");
    var detailAddress = new Address.DetailAddress("1-1-1");
    var address = new Address(zipcode, prefecture, municipalities, detailAddress);
    userDocument =
        new UserDocument(
            accountId, "Taro", "Yamada", email, hashedPassword, address, "090-1234-5678");
    user = new User(new Account.AccountId(accountId), "Taro", "Yamada", address, "090-1234-5678");
  }

  @Test
  void execute_shouldReturnSuccessToken_whenAllStepsSucceed() {
    when(userRepository.findDocumentByEmail(email)).thenReturn(Mono.just(userDocument));
    when(verifyPasswordStep.apply(any(Tuple3.class))).thenReturn(Try.success(accountId));
    when(generateJwtTokenStep.apply(any(User.class))).thenReturn(Try.success(token));

    var resultMono = loginWorkflow.execute(email, rawPassword);

    StepVerifier.create(resultMono)
        .assertNext(
            tryResult -> {
              assertThat(tryResult.isSuccess()).isTrue();
              assertThat(tryResult.get()).isEqualTo(token);
            })
        .verifyComplete();
  }

  @Test
  void execute_shouldReturnUserNotFoundException_whenUserNotFound() {
    when(userRepository.findDocumentByEmail(email)).thenReturn(Mono.empty());

    var resultMono = loginWorkflow.execute(email, rawPassword);

    StepVerifier.create(resultMono)
        .expectErrorMatches(
            throwable ->
                throwable instanceof UserNotFoundException
                    && throwable.getMessage().contains(email))
        .verify();
  }

  @Test
  void execute_shouldReturnInvalidPasswordException_whenPasswordVerificationFails() {
    when(userRepository.findDocumentByEmail(email)).thenReturn(Mono.just(userDocument));
    when(verifyPasswordStep.apply(any(Tuple3.class)))
        .thenReturn(Try.failure(new InvalidPasswordException()));

    var resultMono = loginWorkflow.execute(email, rawPassword);

    StepVerifier.create(resultMono)
        .expectErrorMatches(throwable -> throwable instanceof InvalidPasswordException)
        .verify();
  }

  @Test
  void execute_shouldReturnFailure_whenJwtGenerationFails() {
    var jwtError = new RuntimeException("JWT generation failed");
    when(userRepository.findDocumentByEmail(email)).thenReturn(Mono.just(userDocument));
    when(verifyPasswordStep.apply(any(Tuple3.class))).thenReturn(Try.success(accountId));
    when(generateJwtTokenStep.apply(any(User.class))).thenReturn(Try.failure(jwtError));

    var resultMono = loginWorkflow.execute(email, rawPassword);

    StepVerifier.create(resultMono)
        .assertNext(
            tryResult -> {
              assertThat(tryResult.isFailure()).isTrue();
              assertThat(tryResult.getCause()).isEqualTo(jwtError);
            })
        .verifyComplete();
  }
}
