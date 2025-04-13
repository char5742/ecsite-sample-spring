package com.example.ec_2024b_back.share.domain.models;

/**
 * 住所情報
 *
 * @param zipcode 郵便番号
 * @param prefecture 都道府県
 * @param municipalities 市区町村
 * @param detailAddress 番地以下
 */
public record Address(
    Zipcode zipcode,
    Prefecture prefecture,
    Municipalities municipalities,
    DetailAddress detailAddress) {

  /** 郵便番号 */
  public record Zipcode(String code) {
    public Zipcode {
      if (code.isEmpty()) {
        throw new IllegalArgumentException("郵便番号は空にできません");
      }
      // 正規表現で郵便番号のフォーマットをチェック（例: 123-4567）
      if (!code.matches("\\d{3}-\\d{4}")) {
        throw new IllegalArgumentException("郵便番号のフォーマットが不正です");
      }
    }
  }

  /** 都道府県 */
  public enum Prefecture {
    HOKKAIDO("北海道"),
    AOMORI("青森県"),
    IWATE("岩手県"),
    MIYAGI("宮城県"),
    AKITA("秋田県"),
    YAMAGATA("山形県"),
    FUKUSHIMA("福島県"),
    IBARAKI("茨城県"),
    TOCHIGI("栃木県"),
    GUNMA("群馬県"),
    SAITAMA("埼玉県"),
    CHIBA("千葉県"),
    TOKYO("東京都"),
    KANAGAWA("神奈川県"),
    NIIGATA("新潟県"),
    TOYAMA("富山県"),
    ISHIKAWA("石川県"),
    FUKUI("福井県"),
    YAMANASHI("山梨県"),
    NAGANO("長野県"),
    GIFU("岐阜県"),
    SHIZUOKA("静岡県"),
    AICHI("愛知県"),
    MIE("三重県"),
    SHIGA("滋賀県"),
    KYOTO("京都府"),
    OSAKA("大阪府"),
    HYOGO("兵庫県"),
    NARA("奈良県"),
    WAKAYAMA("和歌山県"),
    TOTTORI("鳥取県"),
    SHIMANE("島根県"),
    OKAYAMA("岡山県"),
    HIROSHIMA("広島県"),
    YAMAGUCHI("山口県"),
    TOKUSHIMA("徳島県"),
    KAGAWA("香川県"),
    EHIME("愛媛県"),
    KOCHI("高知県"),
    FUKUOKA("福岡県"),
    SAGA("佐賀県"),
    NAGASAKI("長崎県"),
    KUMAMOTO("熊本県"),
    OITA("大分県"),
    MIYAZAKI("宮崎県"),
    KAGOSHIMA("鹿児島県"),
    OKINAWA("沖縄県");
    private final String name;

    Prefecture(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public static Prefecture fromName(String name) {
      for (Prefecture pref : Prefecture.values()) {
        if (pref.getName().equals(name)) {
          return pref;
        }
      }
      throw new IllegalArgumentException("Invalid prefecture name: " + name);
    }
  }

  /** 市区町村 */
  public record Municipalities(String value) {
    public Municipalities {
      if (value.isEmpty()) {
        throw new IllegalArgumentException("市区町村は空にできません");
      }
    }
  }

  /** 番地以下の詳細住所 */
  public record DetailAddress(String value) {
    public DetailAddress {
      if (value.isEmpty()) {
        throw new IllegalArgumentException("番地以下の詳細住所は空にできません");
      }
    }
  }
}
