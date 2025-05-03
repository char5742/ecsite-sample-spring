package com.example.ec_2024b_back.userprofile.domain.services;

import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.userprofile.domain.models.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** ユーザープロファイルを作成するファクトリー */
@Component
@RequiredArgsConstructor
public class UserProfileFactory {
  private final IdGenerator idGen;

  /**
   * 新しいユーザープロファイルを作成します
   *
   * @param name ユーザー名
   * @return 作成されたユーザープロファイル
   */
  public UserProfile create(String name) {
    return UserProfile.create(idGen.newId(), name);
  }
}
