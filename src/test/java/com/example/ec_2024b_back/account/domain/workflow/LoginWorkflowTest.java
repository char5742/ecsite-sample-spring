package com.example.ec_2024b_back.account.domain.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.account.domain.models.Account;
import com.example.ec_2024b_back.account.domain.step.GenerateJwtTokenStep;
import com.example.ec_2024b_back.account.domain.step.PasswordInput;
import com.example.ec_2024b_back.account.domain.step.VerifyPasswordStep;
import com.example.ec_2024b_back.account.domain.step.VerifyPasswordStep.InvalidPasswordException;
import com.example.ec_2024b_back.account.domain.workflow.LoginWorkflow.UserNotFoundException;
import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.user.infrastructure.repository.MongoUserRepository;
import com.example.ec_2024b_back.user.infrastructure.repository.document.UserDocument;
import com.example.ec_2024b_back.utils.Fast;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Fast
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class LoginWorkflowTest {

  @Mock private VerifyPasswordStep verifyPasswordStep;
  @Mock private GenerateJwtTokenStep generateJwtTokenStep;
  @Mock private MongoUserRepository userRepository;

  @InjectMocks private LoginWorkflow loginWorkflow;

  private UserDocument userDocument;
  private User user;
  private static final String EMAIL = "test@example.com";
  private static final String RAW_PASSWORD = "password";
  private static final String HASHED_PASSWORD = "hashedPassword";
  private static final String ACCOUNT_ID = "user-id-123";
  private static final String TOKEN = "dummy-jwt-token";

  @BeforeEach
  void setUp() {
    var zipcode = new Address.Zipcode("100-0000");
    var prefecture = Address.Prefecture.TOKYO;
    var municipalities = new Address.Municipalities("千代田区");
    var detailAddress = new Address.DetailAddress("1-1-1");
    var address = new Address(zipcode, prefecture, municipalities, detailAddress);
    userDocument =
        new UserDocument(
            ACCOUNT_ID, "Taro", "Yamada", EMAIL, HASHED_PASSWORD, address, "090-1234-5678");
    user =
        new User(
            new Account.AccountId(ACCOUNT_ID),
            "Taro",
            "Yamada",
            address,
            "090-1234-5678",
            "dummy-password");
    // UserRepositoryのfindByEmailも必ずモックする
    Mockito.when(userRepository.findByEmail(EMAIL))
        .thenReturn(Mono.just(Optional.of(user)));
  }

  @Test
  void execute_shouldReturnSuccessToken_whenAllStepsSucceed() {
    when(userRepository.findDocumentByEmail(EMAIL)).thenReturn(Mono.just(userDocument));
    when(verifyPasswordStep.apply(any(PasswordInput.class))).thenReturn(ACCOUNT_ID);
    when(generateJwtTokenStep.apply(any(User.class))).thenReturn(TOKEN);

    var resultMono = loginWorkflow.execute(EMAIL, RAW_PASSWORD);

    StepVerifier.create(resultMono)
        .assertNext(tokenResult -> assertThat(tokenResult).isEqualTo(TOKEN))
        .verifyComplete();
  }

  @Test
  void execute_shouldReturnUserNotFoundException_whenUserNotFound() {
    when(userRepository.findDocumentByEmail(EMAIL)).thenReturn(Mono.empty());
    // findByEmailもOptional.empty()を返すように上書き
    Mockito.when(userRepository.findByEmail(EMAIL))
        .thenReturn(Mono.just(Optional.empty()));

    var resultMono = loginWorkflow.execute(EMAIL, RAW_PASSWORD);

    StepVerifier.create(resultMono)
        .expectErrorMatches(
            throwable ->
                throwable instanceof UserNotFoundException
                    && throwable.getMessage().contains(EMAIL))
        .verify();
  }

  @Test
  void execute_shouldReturnInvalidPasswordException_whenPasswordVerificationFails() {
    when(userRepository.findDocumentByEmail(EMAIL)).thenReturn(Mono.just(userDocument));
    when(verifyPasswordStep.apply(any(PasswordInput.class)))
        .thenThrow(new InvalidPasswordException());

    var resultMono = loginWorkflow.execute(EMAIL, RAW_PASSWORD);

    StepVerifier.create(resultMono)
        .expectErrorMatches(throwable -> throwable instanceof InvalidPasswordException)
        .verify();
  }

  @Test
  void execute_shouldReturnFailure_whenJwtGenerationFails() {
    var jwtError = new RuntimeException("JWT generation failed");
    when(userRepository.findDocumentByEmail(EMAIL)).thenReturn(Mono.just(userDocument));
    when(verifyPasswordStep.apply(any(PasswordInput.class))).thenReturn(ACCOUNT_ID);
    when(generateJwtTokenStep.apply(any(User.class))).thenThrow(jwtError);

    var resultMono = loginWorkflow.execute(EMAIL, RAW_PASSWORD);

    StepVerifier.create(resultMono)
        .expectErrorMatches(throwable -> throwable.equals(jwtError))
        .verify();
  }
}
