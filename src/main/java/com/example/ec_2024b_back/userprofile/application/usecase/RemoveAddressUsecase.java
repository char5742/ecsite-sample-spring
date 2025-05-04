package com.example.ec_2024b_back.userprofile.application.usecase;

import com.example.ec_2024b_back.userprofile.application.workflow.RemoveAddressWorkflow;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile.UserProfileId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/** 住所削除ユースケースを実装するクラス. */
@Service
@RequiredArgsConstructor
public class RemoveAddressUsecase {

  private final RemoveAddressWorkflow removeAddressWorkflow;
  private final ApplicationEventPublisher event;

  /**
   * ユーザープロファイルから住所を削除する処理を実行し、成功時は更新されたプロファイルを、失敗時はエラーを発行するMonoを返します.
   *
   * @param userProfileId ユーザープロファイルID
   * @param addressId 削除する住所ID
   * @return 更新されたユーザープロファイルを含むMono
   */
  @Transactional
  public Mono<UserProfile> execute(String userProfileId, String addressId) {
    var id = UserProfileId.of(userProfileId);
    return removeAddressWorkflow
        .execute(id, addressId)
        .onErrorMap(
            e ->
                new AddressRemovalFailedException("Failed to remove address: " + e.getMessage(), e))
        .doOnNext(profile -> profile.getDomainEvents().forEach(event::publishEvent));
  }

  /** 住所削除失敗を表すカスタム例外. */
  public static class AddressRemovalFailedException extends RuntimeException {
    public AddressRemovalFailedException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
