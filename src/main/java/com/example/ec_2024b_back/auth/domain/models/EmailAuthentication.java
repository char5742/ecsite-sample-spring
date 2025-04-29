package com.example.ec_2024b_back.auth.domain.models;


import java.util.regex.Pattern;

import com.example.ec_2024b_back.share.domain.models.Email;

/**
 * メールアドレス、パスワードを使用した認証
 *
 * @param email メールアドレス
 * @param password ハッシュ済みのパスワード
 */
public record EmailAuthentication(Email email, HashedPassword password) implements Authentication {
  public static final String TYPE = "email";

  @Override
  public String type() {
    return TYPE;
  }

  /** ハッシュ済みのパスワード */
  public record HashedPassword(String value) {
       // bcrypt ハッシュ形式の基本的なパターン ($2a$, $2b$, $2y$ のいずれかで始まり、コストファクターが続く)
    // 注意: これは完全な検証ではなく、形式のチェックです。
    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[aby]\\$(\\d{2})\\$.{53}$");
    
     /**
     * HashedPassword のコンストラクタ。
     * value が bcrypt 形式に合致するかを簡易的にチェックします。
     *
     * @param value ハッシュ化されたと期待されるパスワード文字列
     * @throws IllegalArgumentException value が期待される bcrypt 形式でない場合
     */
    public HashedPassword { // レコードのコンパクトコンストラクタ
      if (!BCRYPT_PATTERN.matcher(value).matches()) {
        throw new IllegalArgumentException("パスワードは bcrypt 形式でなければなりません");
      }
    }
  }
}
