# 6. コーディング規約ガイド

このドキュメントでは、`ecsite-v2` プロジェクトにおけるコーディング規約とベストプラクティスについて説明します。一貫性のあるコードは、可読性、保守性を高め、チーム開発を円滑にします。

## 基本原則

*   **可読性:** コードは他の開発者が容易に理解できるように記述します。
*   **保守性:** 将来の変更や機能追加が容易に行えるように設計します。
*   **一貫性:** プロジェクト全体で統一されたスタイルとパターンに従います。
*   **シンプルさ:** 必要以上に複雑なコードは避け、簡潔な表現を心がけます。

## 命名規則

*   **パッケージ:** 小文字のスネークケース (例: `com.example.ec_2024b_back.auth.domain.models`)。
*   **クラス・インターフェース・Enum・Record:** アッパーキャメルケース (例: `Account`, `Accounts`, `LoginWorkflow`)。
*   **メソッド:** ローワーキャメルケース (例: `findByEmail`, `generateToken`)。
*   **定数:** アッパースネークケース (例: `DEFAULT_TIMEOUT`)。
*   **変数 (ローカル変数、フィールド):** ローワーキャメルケース (例: `targetAccount`, `expirationMillis`)。
*   **テストクラス:** テスト対象クラス名 + `Test` (例: `LoginWorkflowTest`)。
*   **テストメソッド:** `should` + 期待される振る舞い (例: `shouldReturnTokenWhenCredentialsAreValid`)。

## フォーマット

*   当プロジェクトでは [Spotless](https://github.com/diffplug/spotless) (version 7.0.3) を使用してコードフォーマットを自動的に適用します。
*   設定ファイル: `spotless.gradle` (具体的なルールはファイル参照)。
*   適用コマンド: `./gradlew spotlessApply`
*   チェックコマンド: `./gradlew spotlessCheck`
*   Lefthook により、コミット前に `spotlessApply` が自動実行されます。原則として、手動でのフォーマット調整は不要です。

## コメント

*   **JavaDoc:** public なクラス、メソッド、重要なフィールドには JavaDoc コメントを記述します。
    *   目的、パラメータ (`@param`)、戻り値 (`@return`)、スローされる例外 (`@throws`) を明確に記述します。
    *   日本語で記述し、文末にはピリオド (`.`) をつけます。
    *   例:
        ```java
        /**
         * 指定されたメールアドレスに紐づくアカウントを検索します.
         *
         * @param email 検索対象のメールアドレス (nullであってはならない).
         * @return 見つかったアカウントを含む Mono、見つからない場合は Mono.empty().
         */
        Mono<Account> findByEmail(Email email);
        ```
*   **インラインコメント:** 複雑なロジックや、一見して意図が分かりにくい箇所には `//` を用いて簡潔な説明を追加します。冗長なコメントは避けます。
*   **TODO コメント:** `// TODO:` の形式で、将来対応すべきタスクや改善点を記述します。可能であれば、担当者名や関連Issue番号を追記します。

## エラーメッセージと表示テキスト

*   **言語**: すべてのエラーメッセージ、ログメッセージ、ユーザー向けテキストは原則として日本語で記述してください。
    *   例外スローの際のメッセージ: `throw new IllegalArgumentException("null の AccountDocument を Account に変換することはできません");`
    *   カスタム例外のメッセージ: `super("メールアドレス: " + email + " のアカウントが見つかりません");` (例)
*   **フォーマット**:
    *   変数値を含める場合は、「: 」（コロンの後にスペース）で区切ってください。
    *   例: `"メールアドレス: " + email + " のアカウントが見つかりません"`
*   **例外メッセージの注意点**:
    *   機密情報（完全なパスワード、トークン、接続文字列など）を含めないでください。
    *   デバッグ情報として有用な識別子（アカウントID、メールアドレスなど）は含めることを推奨します。
*   **コメント**:
    *   コードコメントも同様に日本語で記述してください。

## Java コーディング規約

### `var` の使用基準

Javaの型推論キーワード `var` は以下のガイドラインに従って使用してください：

*   **推奨される使用場面:**
    *   ローカル変数の宣言時（メソッド内の変数）。
    *   右辺から型が明白な場合（例: `var account = new Account(...)`、`var token = jwtProvider.generate(...)`）。
    *   ジェネリクスを含む型名が長く、コードの可読性を損ねる場合。
    *   ストリーム処理の中間変数。
*   **避けるべき使用場面:**
    *   フィールド宣言（クラスのメンバ変数）- Javaの言語仕様として不可能。
    *   メソッドの戻り値型、メソッドパラメータの型。
    *   右辺から型が推測しづらい場合（メソッド呼び出しの結果など）。変数名で型が明確にわかる場合を除く。
    *   数値リテラルや `null` のみを代入する場合（例: `var count = 0;` は `int count = 0;` とする）。
    *   ダイヤモンド演算子と組み合わせる場合（例: `var map = new HashMap<>();` は避ける）。
*   **例:**
    ```java
    // 良い例
    var account = accountsRepository.findByEmail(email); // Mono<Account> が明白
    var jwtProperties = new JWTProperties("secret", 3600000L); // 型が明白
    var authenticationList = account.authentications().stream()...; // ストリーム処理

    // 避けるべき例
    var result = someLegacyMethod(); // 戻り値の型が不明瞭
    var id = account.id(); // 基本型や単純な型の場合は明示的に書く方が良い場合がある (例: AccountId id = ...)
    ```
*   **注意点:**
    *   `var` を使う場合でも、変数名から型や意図が推測できるような命名を心がけてください。
    *   テストコードでも同様のガイドラインに従います。

### Stream API, Optional, 例外処理, Lombok のガイドライン

*   **Stream API:** 積極的に活用し、コレクション操作を宣言的に記述します。中間操作と終端操作を意識し、可読性を損なわない範囲で使用します。複雑すぎるラムダ式はメソッド抽出を検討します。
*   **Optional:** メソッドの戻り値として「値が存在しない可能性がある」ことを明示する場合に使用します。`null` を返す代わりに `Optional.empty()` を返します。
    *   `Optional` をフィールドやメソッド引数に使用することは原則として避けます (NullAway による非null保証を活用)。
    *   `isPresent()` チェックと `get()` の組み合わせよりも、`map()`, `flatMap()`, `filter()`, `orElse()`, `orElseGet()`, `orElseThrow()` などのメソッドを活用します。
*   **例外処理:**
    *   チェック例外 (Checked Exception) よりも非チェック例外 (Unchecked Exception) を中心に使用します (特にビジネスロジック層)。
    *   ドメイン固有の例外は `DomainException` (share モジュール) を継承して作成します。
    *   例外メッセージには、問題解決に役立つ情報（原因、関連IDなど）を含めますが、機密情報は含めません。
    *   Reactor でのエラーハンドリング (`onErrorResume`, `onErrorMap` など) を適切に使用します。
*   **Lombok:** ボイラープレートコード削減のために活用します。
    *   `@Getter`, `@Setter` (必要な場合のみ), `@ToString`, `@EqualsAndHashCode` を適切に使用します。
    *   不変クラスには `@Value` または `@Getter` + `@RequiredArgsConstructor` / `@AllArgsConstructor` を使用します (Record クラスも推奨)。
    *   ロギングには `@Slf4j` を使用します。

## 設定値の取得（Properties）

本プロジェクトでは、設定値（`application.properties`に記述される値）の取得方法として、以下のガイドラインに従ってください：

*   **推奨: `@ConfigurationProperties` を使用したクラス:**
    *   関連する設定値を専用のクラスに集約し、型安全なアクセスを提供します。
    *   `@Validated` アノテーションと `jakarta.validation.constraints` アノテーション (例: `@NotNull`) を用いて、起動時に設定値の検証を行います。
    *   例 (`auth` モジュールの `JWTProperties`):
        ```java
        package com.example.ec_2024b_back.auth.infrastructure.security;

        import jakarta.validation.constraints.NotNull;
        import lombok.AllArgsConstructor;
        import lombok.Getter;
        import org.springframework.boot.context.properties.ConfigurationProperties;
        import org.springframework.validation.annotation.Validated;

        @Validated
        @Getter
        @AllArgsConstructor
        @ConfigurationProperties("jwt") // "jwt" プレフィックスを持つプロパティをバインド
        public class JWTProperties {
          /** JWTのシークレットキー */
          @NotNull private final String secret;

          /** JWTの有効期限（ミリ秒） */
          @NotNull private final Long expirationMillis;
        }
        ```
    *   `@Value` アノテーションによる個別のプロパティ取得は、設定箇所が少なく、検証が不要な場合に限定して使用します。

## 静的解析とバグ検出

本プロジェクトでは、コードの品質向上と潜在的なバグの早期発見のため、複数の静的解析ツールを採用しています。

### Error Prone

[Error Prone](https://errorprone.info/) (version 2.37.0) は、Googleが開発したJavaコードの静的解析ツールで、コンパイル時に一般的なプログラミングエラーを検出します。

*   **設定方法**:
    *   `build.gradle` ファイル内で `net.ltgt.errorprone` Gradleプラグインと依存関係が設定されています。
*   **チェック方法**:
    *   通常のビルドプロセス (`./gradlew build`) 中に自動的に実行されます。

### NullAway

[NullAway](https://github.com/uber/NullAway) (version 0.12.6) は、Uberが開発したError Proneプラグインで、null参照例外（NullPointerException）をコンパイル時に検出するためのツールです。

*   **設定概要**:
    *   `build.gradle` 内で Error Prone のチェックとして有効化 (`check("NullAway", CheckSeverity.ERROR)`) されています。
    *   検査対象パッケージは `com.example.ec_2024b_back` (`option("NullAway:AnnotatedPackages", ...)` で指定) です。
    *   テストコード (`src/test/java`) では NullAway は無効化 (`disable("NullAway")`) されています。
*   **パッケージレベルのnull安全性設定**:
    *   プロジェクトのルートパッケージ (`com.example.ec_2024b_back`) および各サブパッケージ (`auth`, `share` など) の `package-info.java` ファイルで `@NullMarked` (JSpecify アノテーション) を適用しています。
    *   これにより、これらのパッケージ内のコードはデフォルトで **non-null** として扱われます。
    *   開発者は、null を許容したいフィールド、メソッド引数、戻り値に対して明示的に `@Nullable` アノテーションを付与する必要があります。
*   **アノテーション**:
    *   プロジェクトでは [JSpecify](https://jspecify.dev/) (version 1.0.0) のアノテーションを採用しています:
        *   `@NullMarked`: パッケージまたはクラスレベルで適用し、デフォルトで non-null として扱われることを示す。
        *   `@Nullable`: null である可能性があることを示す。
        *   `@NullUnmarked`: クラスレベルで適用し、そのクラス内での nullability チェックを一時的に緩和する (例: 外部ライブラリやフレームワークとの境界、ドキュメントクラスなど)。
    *   使用例:
        ```java
        // MongoDB ドキュメントクラスで Null チェックを緩和
        @NullUnmarked
        @Document(collection = "accounts")
        public class AccountDocument { ... }

        // メソッド引数で null を許容
        public Mono<Account> findByEmail(@Nullable Email email) {
            if (email == null) {
                return Mono.empty(); // またはエラー処理
            }
            // ...
        }
        ```
*   **nullチェックのベストプラクティス**:
    *   `@NullMarked` 環境下では、`@Nullable` が付いていない型は non-null であると仮定できます。
    *   `@Nullable` が付いている引数やメソッド呼び出し結果に対しては、使用前に必ず null チェックを行うか、`Optional` を利用します。
    *   コンストラクタやメソッドの入口で `@Nullable` でない引数の null チェックを行う必要は基本的にありません (NullAway が保証)。
    *   ドメインモデルでは基本的にすべてのフィールドを non-null とし、`@Nullable` の使用は最小限に留めます。
    *   MongoDB ドキュメントクラス (`*Document`) など、外部データとのマッピングを行うクラスでは `@NullUnmarked` を使用することがあります。

### 運用ガイドライン

*   **CI/CD統合**: プルリクエスト時に自動的に静的解析 (Error Prone, NullAway) が実行されます。ビルドエラーが発生した場合は修正が必要です。
*   **違反の修正**:
    *   Error Prone の警告は原則として無視せず、コードを修正するか、正当な理由がある場合のみ `@SuppressWarnings("...")` を使用して抑制します。
    *   NullAway のエラーは厳密に対応し、null の可能性がある場合は `@Nullable` でマークし、呼び出し側で適切に処理します。
*   **NullAway違反の修正例**:
    ```java
    // インフラ層のリポジトリメソッド (null を返す可能性があるとする)
    @Nullable
    AccountDocument findDocumentByEmail(String email);

    // ドメイン層のリポジトリメソッド (Optional を返す)
    public Mono<Optional<Account>> findByEmail(Email email) {
        // MongoAccounts 実装内で NullAway エラーが出ないように修正
        return reactiveMongoRepository.findAccountDocumentByAuthenticationsEmail(email.value())
            .map(doc -> Optional.of(doc.toDomain())) // mapNotNull 的な処理や orElse(Optional.empty())
            .defaultIfEmpty(Optional.empty());
    }

    // アプリケーション層での利用 (orElseThrow などで処理)
    public Mono<LoginResponse> execute(LoginRequest request) {
        return accountsRepository.findByEmail(request.email())
            .flatMap(optionalAccount -> optionalAccount
                .map(Mono::just)
                .orElseGet(() -> Mono.error(new AccountNotFoundException("メールアドレス: " + request.email() + " のアカウントが見つかりません")))
            )
            // ... 後続処理
    }
    ```
*   **ビルド/コンパイル時のエラー対応**:
    *   NullAway エラーは明確なメッセージとソースコード位置を表示します。
    *   ビルドログを確認し、指摘された null 安全性に関する問題を修正してください。
