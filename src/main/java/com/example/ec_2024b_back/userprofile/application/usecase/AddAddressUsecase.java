package com.example.ec_2024b_back.userprofile.application.usecase;

import com.example.ec_2024b_back.userprofile.application.workflow.AddAddressWorkflow;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile.UserProfileId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/** 住所追加ユースケースを実装するクラス. */
@Service
@RequiredArgsConstructor
public class AddAddressUsecase {

  private final AddAddressWorkflow addAddressWorkflow;
  private final ApplicationEventPublisher event;

  /**
   * ユーザープロファイルに住所を追加する処理を実行し、成功時は更新されたプロファイルを、失敗時はエラーを発行するMonoを返します.
   *
   * @param userProfileId ユーザープロファイルID
   * @param name 住所名称
   * @param postalCode 郵便番号
   * @param prefecture 都道府県
   * @param city 市区町村
   * @param street 町名・番地
   * @param building 建物名・部屋番号（任意）
   * @param phoneNumber 電話番号
   * @param isDefault デフォルト住所かどうか
   * @return 更新されたユーザープロファイルを含むMono
   */
  @Transactional
  public Mono<UserProfile> execute(
      String userProfileId,
      String name,
      String postalCode,
      String prefecture,
      String city,
      String street,
      String building,
      String phoneNumber,
      boolean isDefault) {

    var id = UserProfileId.of(userProfileId);
    return addAddressWorkflow
        .execute(id, name, postalCode, prefecture, city, street, building, phoneNumber, isDefault)
        .onErrorMap(
            e -> new AddressAdditionFailedException("Failed to add address: " + e.getMessage(), e))
        .doOnNext(profile -> profile.getDomainEvents().forEach(event::publishEvent));
  }

  /** 住所追加失敗を表すカスタム例外. */
  public static class AddressAdditionFailedException extends RuntimeException {
    public AddressAdditionFailedException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
