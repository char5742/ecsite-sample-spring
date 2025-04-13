# 6. コーディング規約ガイド

このドキュメントでは、`ecsite-v2` プロジェクトにおけるコーディング規約とベストプラクティスについて説明します。一貫性のあるコードは、可読性、保守性を高め、チーム開発を円滑にします。

*(現在執筆中です)*

## 基本原則

*(可読性、保守性、一貫性などの基本方針を記載予定)*

## 命名規則

*(パッケージ、クラス、メソッド、変数などの命名規則を記載予定)*

## フォーマット

*   当プロジェクトでは [Spotless](https://github.com/diffplug/spotless) を使用してコードフォーマットを自動的に適用します。
*   設定ファイル: `spotless.gradle`
*   適用コマンド: `./gradlew spotlessApply`
*   チェックコマンド: `./gradlew spotlessCheck`
*   コミット前に `spotlessApply` を実行し、コードスタイルを統一してください。
*(具体的なフォーマットルール (インデント、括弧の位置など) の詳細を記載予定)*

## コメント

*(Javadocの書き方、コメントすべき箇所、避けるべきコメントなどを記載予定)*

## エラーメッセージと表示テキスト

* **言語**: すべてのエラーメッセージ、ログメッセージ、ユーザー向けテキストは原則として日本語で記述してください。
  * 例外スローの際のメッセージ: `throw new IllegalArgumentException("null のUserDocumentをUserに変換することはできません");`
  * カスタム例外のメッセージ: `super("メールアドレス: " + email + " のユーザーが見つかりません");`

* **フォーマット**:
  * 変数値を含める場合は、「: 」（コロンの後にスペース）で区切ってください。
  * 例: `"メールアドレス: " + email + " のユーザーが見つかりません"`

* **例外メッセージの注意点**:
  * 機密情報（完全なパスワード、トークン、接続文字列など）を含めないでください。
  * デバッグ情報として有用な識別子（ID、メールアドレス、リクエスト識別子など）は含めることを推奨します。

* **コメント**:
  * コードコメントも同様に日本語で記述してください。
  * JavaDocコメントは日本語で記述し、末尾にピリオド（.）をつけてください。

## Java コーディング規約

### `var` の使用基準

Javaの型推論キーワード `var` は以下のガイドラインに従って使用してください：

* **推奨される使用場面:**
  * ローカル変数の宣言時（メソッド内の変数）
  * 型名が長く、コードの可読性を損ねる場合（例: `Map<String, List<SomeComplexType>>` など）
  * 右辺から型が明白な場合（例: `var user = new User()` など）
  * ストリーム処理や関数型インターフェースを使用したラムダ式の中間変数

* **避けるべき使用場面:**
  * フィールド宣言（クラスのメンバ変数）- Javaの言語仕様として不可能
  * メソッドの戻り値型
  * メソッドパラメータの型
  * 右辺から型が推測しづらい場合
  * 数値リテラルやnullのみを代入する場合（例: `var x = 0;` - この場合は明示的に `int x = 0;` とする）

* **例:**
  ```java
  // 良い例
  var users = userRepository.findAll();  // 型が明白
  var serviceResult = new ServiceResult<UserDto>();  // 冗長な型名の繰り返しを避ける
  
  // 避けるべき例
  var result = someMethod();  // 型が不明瞭
  var id = getId();  // 基本型の場合は明示的に型を書く方が良い
  ```

* **注意点:**
  * `var` を使う場合でも、変数名から型が推測できるような命名を心がけてください。
  * テストコードでも同様のガイドラインに従います。

### Stream API, Optional, 例外処理, Lombok のガイドライン

*(記載予定)*

## 設定値の取得（Properties）

本プロジェクトでは、設定値（`application.properties`に記述される値）の取得方法として、以下のガイドラインに従ってください：

*   **推奨: Propertiesクラスを使用:**
    *   設定値は直接`@Value`アノテーションで取得するのではなく、専用のPropertiesクラスに集約します。
    *   例:
      ```java
      @Validated
      @Data
      @Configuration("jwt")
      public class JWTProperties {
        /** JWTのシークレットキー */
        @NotNull private String secret;

        /** JWTの有効期限（ミリ秒） */
        @NotNull private Long expirationMillis;
      }
      ```

## 静的解析とバグ検出

本プロジェクトでは、コードの品質向上と潜在的なバグの早期発見のため、複数の静的解析ツールを採用しています。

### Error Prone

[Error Prone](https://errorprone.info/)は、Googleが開発したJavaコードの静的解析ツールで、コンパイル時に一般的なプログラミングエラーを検出します。

* **設定方法**:
  * プロジェクトでは`build.gradle`ファイル内で設定されています
  * 依存関係: `errorprone "com.google.errorprone:error_prone_core:2.37.0"`
  * Gradleプラグイン: `net.ltgt.errorprone`を使用

* **エラー重要度レベル**:
  * `ERROR`: ビルドが失敗するレベルのエラー
  * `WARNING`: 警告として表示されるが、ビルドは続行する

* **チェック方法**:
  * 通常のビルドプロセス中に自動的に実行: `./gradlew build`
  * 単独で実行することも可能: `./gradlew compileJava`

### NullAway

[NullAway](https://github.com/uber/NullAway)は、Uberが開発したError Proneプラグインで、null参照例外（NullPointerException）を検出するためのツールです。

* **設定概要**:
  * `build.gradle`内に以下の設定があります:
  ```gradle
  tasks.withType(JavaCompile) {
    options.errorprone {
      check("NullAway", CheckSeverity.ERROR)
      option("NullAway:AnnotatedPackages", "com.example.ec_2024b_back")
      excludedPaths = ".*build/generated/.*"
    }
    // テストコードではNullAwayを無効化
    if (name.toLowerCase().contains("test")) {
      options.errorprone {
        disable("NullAway")
      }
    }
  }
  ```

* **主要設定パラメータ**:
  * `AnnotatedPackages`: NullAwayの検査対象となるパッケージ
  * `excludedPaths`: 検査から除外するパス（OpenAPI Generatorで生成されたコードなど）
  * テストコードでは検査が無効化されています

* **パッケージレベルのnull安全性設定**:
  * プロジェクトでは、パッケージレベルで`@NullMarked`アノテーションを適用して、そのパッケージ内のすべてのクラスでデフォルトで非null型として扱われるようにしています:
  ```java
  // package-info.javaファイル
  @NullMarked
  package com.example.ec_2024b_back;
  
  import org.jspecify.annotations.NullMarked;
  ```
  * この設定により、明示的に`@Nullable`が付与されていない限り、すべてのフィールド、引数、戻り値はnon-nullとして扱われます
  * 開発者は`@Nullable`を明示的に使用してnullを許容する箇所を示す必要があります

* **アノテーション**:
  * プロジェクトでは[JSpecify](https://jspecify.dev/)のアノテーションを採用しています:
    * `@NullMarked`: パッケージまたはクラスレベルで適用し、デフォルトですべての型がnon-nullとして扱われることを示す
    * `@Nullable`: nullである可能性があることを示す
    * `@NullUnmarked`: nullabilityのチェックを一時的に緩和するクラスを示す
  * 使用例:
    ```java
    // クラスレベルでnullチェックを緩和
    @NullUnmarked
    public class UserDocument {
      // フィールド
    }
    
    // 個別の変数でnull許容を明示
    public User findByEmail(@Nullable String email) {
      // メソッド内で適切なnullチェックが必要
    }
    ```

* **nullチェックのベストプラクティス**:
  * コンストラクタで引数の非nullチェックを行う
  * Optional<T>を適切に使用する
  * 冗長なnullチェックを避ける
  * ドメインモデルでは基本的にすべてのフィールドを非nullとする
  * MongoDBドキュメントクラスなど、外部データソースとのインターフェースでは@NullUnmarkedを使用することができる

### 運用ガイドライン

* **CI/CD統合**: プルリクエスト時に自動的に静的解析が実行されます
* **違反の修正**:
  * Error Proneの警告は原則として無視せず、コードを修正するか、必要な場合のみ`@SuppressWarnings("ErrorProneCheck")`を使用して抑制
  * NullAwayの警告は厳密に対応し、nullの可能性がある場合は適切にマークし、処理する

* **NullAway違反の修正例**:
  ```java
  // 不適切な例: null検査なし
  public User findByEmail(String email) {
    UserDocument doc = repository.findByEmail(email);
    return convertToDomain(doc); // docがnullの場合NPE発生
  }
  
  // 適切な例1: Optional使用
  public Optional<User> findByEmail(String email) {
    return Optional.ofNullable(repository.findByEmail(email))
      .map(this::convertToDomain);
  }
  
  // 適切な例2: 明示的なnull検査
  public User findByEmail(String email) {
    UserDocument doc = repository.findByEmail(email);
    if (doc == null) {
      throw new UserNotFoundException("メールアドレス: " + email + " のユーザーが見つかりません");
    }
    return convertToDomain(doc);
  }
  ```

* **ビルド/コンパイル時のエラー対応**:
  * NullAwayエラーは明確なメッセージとソースコード位置を表示します
  * ビルドログを確認し、指摘された問題を修正してください
