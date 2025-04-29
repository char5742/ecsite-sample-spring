# 7. テストガイド

このドキュメントでは、`ecsite-v2` プロジェクトにおけるテスト戦略、各種テストの実装方法、およびテストに関するガイドラインについて説明します。

## テスト戦略

本プロジェクトでは、一般的に「テストピラミッド」と呼ばれる考え方に基づき、以下の階層でテストを構成します。

1.  **単体テスト (Unit Tests):** 最も数が多く、高速に実行できるテスト。個々のクラスやメソッドのロジックを検証します。外部依存はモック化します。
2.  **結合テスト (Integration Tests):** 複数のコンポーネントやモジュール、または外部システム (DBなど) との連携を検証するテスト。単体テストより数は少なく、実行に時間がかかります。
3.  **E2Eテスト (End-to-End Tests):** (現在は実施していません) 実際のユーザー操作を模倣し、システム全体の動作を検証するテスト。最も実行コストが高いです。

## 単体テスト (Unit Tests)

*   **対象:**
    *   ドメインモデル (`Account`, `EmailAuthentication` など): 不変性、バリデーション、ビジネスロジックの検証。
    *   Workflow (`LoginWorkflow` など): 依存する Step をモック化し、ステップ呼び出し順序やロジックフローを検証。
    *   Usecase (`LoginUsecase` など): 依存する Workflow や Repository をモック化し、アプリケーション層のロジックを検証。
    *   Step インターフェースのテスト (モックを使用): Step の振る舞いを定義通りに呼び出すかの検証。
    *   ユーティリティクラスなど。
*   **ツール:** JUnit 5, Mockito (モック化), AssertJ (アサーション)。
*   **リアクティブコードのテスト:** `StepVerifier` を使用して `Mono` や `Flux` の発行するシグナルを検証します。
*   **実装例:**
    *   `src/test/java/com/example/ec_2024b_back/auth/domain/models/EmailAuthenticationTest.java`
    *   `src/test/java/com/example/ec_2024b_back/auth/domain/workflow/LoginWorkflowTest.java`
    *   `src/test/java/com/example/ec_2024b_back/auth/application/usecase/LoginUsecaseTest.java`

## 結合テスト (Integration Tests)

*   **対象:**
    *   Repository 実装 (`MongoAccounts` など): 実際の DB (Testcontainers で起動) との連携を検証。
    *   Step 実装 (`FindAccountByEmailStepImpl` など): DB や他のインフラコンポーネントとの連携を含む場合の検証。
    *   API ハンドラー/コントローラー層: リクエスト受付からレスポンス返却までの一連の流れを検証 (DB 連携含む)。
*   **ツール/アノテーション:**
    *   `@SpringBootTest`: Spring Boot アプリケーションコンテキスト全体をロードしてテストを実行します。API エンドポイントのテストなどに使用します。
    *   `@DataMongoTest`: MongoDB との連携に特化したテストスライス。Repository 層のテストに使用します。
    *   `@IntegrationTest` (`com.example.ec_2024b_back.utils.IntegrationTest`): `@SpringBootTest` と `@ActiveProfiles("test")` を組み合わせたカスタムアノテーション。主に API 層のテストで使用されることを想定していますが、現状 `application-test.properties` がないため、プロファイル指定の効果は限定的かもしれません。
    *   Testcontainers: Docker コンテナを使用してテスト用の MongoDB インスタンスを起動します (`TestMongoConfig` で設定)。これにより、実際の DB 環境に近い状態でテストを実行できます。
    *   `WebTestClient`: リアクティブな API エンドポイントをテストするためのクライアント。
*   **テスト用プロファイル:** 現在 `src/test/resources` に `application-test.properties` は存在しません。テスト固有の設定が必要な場合は、Testcontainers の設定やテストクラス内でのプロパティ設定で行います。
*   **実装例:**
    *   Repository 層のテスト (`MongoAccounts` など) は現在未実装です。実装する場合は `@DataMongoTest` と Testcontainers を使用します。
    *   Controller/Handler 層のテストも現在未実装です。実装する場合は `@IntegrationTest` (または `@SpringBootTest`) と `WebTestClient` を使用します。

## E2Eテスト (End-to-End Tests)

*   現在は実施していません。

## テスト実装ガイドライン

*   **命名規則:**
    *   テストクラス: `TargetClassNameTest` (例: `LoginWorkflowTest`)。
    *   テストメソッド: `should` + 期待される振る舞い + `when` + 条件 (例: `shouldReturnTokenWhenCredentialsAreValid`)。日本語での記述も可。
*   **構造:** Given-When-Then (または Arrange-Act-Assert) パターンを推奨します。テストメソッド内で準備、実行、検証の各ステップを明確に分離します (コメントや空行で区切るなど)。
*   **カバレッジ:**
    *   テストカバレッジの計測には JaCoCo Gradle プラグインを使用します。レポートは `./gradlew test jacocoTestReport` を実行後、`build/reports/jacoco/test/html/index.html` で確認できます。
    *   GitHub Actions 上でのカバレッジレポート生成・コメント投稿には [Octocov](https://github.com/k1LoW/octocov) を使用しています。設定はプロジェクトルートの `.octocov.yml` で行われています。
    *   目標カバレッジはプロジェクトのポリシーに従いますが、重要なドメインロジックや複雑な処理については高いカバレッジを目指します。
*   **実行速度:**
    *   テストの実行速度に応じて、テストクラスまたはメソッドにカスタムアノテーション `@Fast` または `@Slow` (`com.example.ec_2024b_back.utils` パッケージ参照) を付与することを推奨します。
    *   `@Fast`: 主に単体テスト。
    *   `@Slow`: 主に結合テスト (DB 起動など時間のかかるもの)。
    *   CI 環境などで `./gradlew test -Pfast` のように実行するテストを絞り込む際に利用できます (Gradle タスク設定が必要)。
*   **その他:**
    *   テストデータは各テストメソッド内で準備するか、`@BeforeEach` を使用します。テストデータ生成ユーティリティの作成も検討します。
    *   アサーションには AssertJ を活用し、流暢で読みやすい検証コードを記述します。
