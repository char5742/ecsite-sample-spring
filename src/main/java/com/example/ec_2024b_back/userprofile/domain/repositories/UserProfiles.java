package com.example.ec_2024b_back.userprofile.domain.repositories;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile.UserProfileId;
import org.jmolecules.ddd.types.Repository;
import reactor.core.publisher.Mono;

public interface UserProfiles extends Repository<UserProfile, UserProfileId> {
  // IDでユーザープロファイルを検索するメソッド
  Mono<UserProfile> findById(UserProfileId id);

  // アカウントIDでユーザープロファイルを検索するメソッド
  Mono<UserProfile> findByAccountId(AccountId accountId);

  // ユーザープロファイルを保存するメソッド
  Mono<UserProfile> save(UserProfile userProfile);
}
