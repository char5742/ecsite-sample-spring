package com.example.ec_2024b_back.account.infrastructure.stepimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.account.domain.models.Account;
import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.share.domain.models.Address.Zipcode;
import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.user.infrastructure.repository.MongoUserRepository;
import com.example.ec_2024b_back.utils.Fast;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@Fast
class FindUserByEmailStepImplTest {

  @Mock private MongoUserRepository userRepository;

  @InjectMocks private FindUserByEmailStepImpl findUserByEmailStep;

  private final String testEmail = "test@example.com";
  private final User testUser =
      new User(
          new Account.AccountId("test-id"),
          "Taro",
          "Yamada",
          new Address(
              new Zipcode("100-0000"),
              Address.Prefecture.TOKYO,
              new Address.Municipalities("Chiyoda"),
              new Address.DetailAddress("1-1-1")),
          "090-1234-5678");

  @Test
  void apply_shouldReturnSuccessWithUser_whenUserFound() {
    // Arrange
    when(userRepository.findByEmail(anyString()))
        .thenReturn(Mono.just(Try.success(Option.of(testUser))));

    // Act
    var resultMono = findUserByEmailStep.apply(testEmail);

    // Assert
    StepVerifier.create(resultMono)
        .assertNext(
            resultTry -> {
              assertThat(resultTry.isSuccess()).isTrue();
              assertThat(resultTry.get().isDefined()).isTrue();
              assertThat(resultTry.get().get()).isEqualTo(testUser);
            })
        .verifyComplete();
  }

  @Test
  void apply_shouldReturnSuccessWithNone_whenUserNotFound() {
    // Arrange
    when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(Try.success(Option.none())));

    // Act
    var resultMono = findUserByEmailStep.apply(testEmail);

    // Assert
    StepVerifier.create(resultMono)
        .assertNext(
            resultTry -> {
              assertThat(resultTry.isSuccess()).isTrue();
              assertThat(resultTry.get().isEmpty()).isTrue();
            })
        .verifyComplete();
  }

  @Test
  void apply_shouldReturnFailure_whenRepositoryThrowsException() {
    // Arrange
    var exception = new RuntimeException("DB error");
    when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(Try.failure(exception)));

    // Act
    var resultMono = findUserByEmailStep.apply(testEmail);

    // Assert
    StepVerifier.create(resultMono)
        .assertNext(
            resultTry -> {
              assertThat(resultTry.isFailure()).isTrue();
              assertThat(resultTry.getCause()).isEqualTo(exception);
            })
        .verifyComplete();
  }

  @Test
  void apply_shouldReturnFailure_whenRepositoryReturnsErrorMono() {
    // Arrange
    var exception = new RuntimeException("DB error on Mono");
    when(userRepository.findByEmail(anyString())).thenReturn(Mono.error(exception));

    // Act
    var resultMono = findUserByEmailStep.apply(testEmail);

    // Assert
    StepVerifier.create(resultMono).expectError(RuntimeException.class).verify();
  }
}
