package com.example.ec_2024b_back.account.infrastructure.stepimpl;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ec_2024b_back.account.domain.models.Account;
import com.example.ec_2024b_back.share.domain.models.Address;
import com.example.ec_2024b_back.user.domain.models.User;
import com.example.ec_2024b_back.user.infrastructure.repository.MongoUserRepository;
import com.example.ec_2024b_back.utils.Fast;

import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Fast
@ExtendWith(MockitoExtension.class)
class FindUserByEmailStepImplTest {

  @Mock private MongoUserRepository userRepository;

  @InjectMocks private FindUserByEmailStepImpl findUserByEmailStep;

  private User user;
  private String email = "test@example.com";

  @BeforeEach
  void setUp() {
    var zipcode = new Address.Zipcode("100-0000");
    var prefecture = Address.Prefecture.TOKYO;
    var municipalities = new Address.Municipalities("テスト区");
    var detailAddress = new Address.DetailAddress("テスト1-1");
    var address = new Address(zipcode, prefecture, municipalities, detailAddress);
    user = new User(new Account.AccountId("user-id-123"), "Test", "User", address, "000-0000-0000");
  }

  @Test
  void apply_shouldReturnSuccessWithUserOption_whenRepositoryReturnsUser() {
    var repositoryResult = Mono.just(Try.success(Option.of(user)));
    when(userRepository.findByEmail(anyString())).thenReturn(repositoryResult);

    var resultMono = findUserByEmailStep.apply(email);

    StepVerifier.create(resultMono)
        .assertNext(
            tryResult -> {
              assertThat(tryResult.isSuccess()).isTrue();
              assertThat(tryResult.get().isDefined()).isTrue();
              assertThat(tryResult.get().get()).isEqualTo(user);
            })
        .verifyComplete();
  }

  @Test
  void apply_shouldReturnSuccessWithEmptyOption_whenRepositoryReturnsEmpty() {
    var repositoryResult = Mono.just(Try.success(Option.<User>none()));
    when(userRepository.findByEmail(anyString())).thenReturn(repositoryResult);

    var resultMono = findUserByEmailStep.apply(email);

    StepVerifier.create(resultMono)
        .assertNext(
            tryResult -> {
              assertThat(tryResult.isSuccess()).isTrue();
              assertThat(tryResult.get().isEmpty()).isTrue();
            })
        .verifyComplete();
  }

  @Test
  void apply_shouldReturnFailure_whenRepositoryReturnsFailure() {
    var exception = new RuntimeException("DB error");
    var repositoryResult = Mono.just(Try.<Option<User>>failure(exception));
    when(userRepository.findByEmail(anyString())).thenReturn(repositoryResult);

    var resultMono = findUserByEmailStep.apply(email);

    StepVerifier.create(resultMono)
        .assertNext(
            tryResult -> {
              assertThat(tryResult.isFailure()).isTrue();
              assertThat(tryResult.getCause()).isEqualTo(exception);
            })
        .verifyComplete();
  }
}
