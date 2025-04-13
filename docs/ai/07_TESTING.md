# 7. テストガイド

このドキュメントでは、`ecsite-v2` プロジェクトにおけるテスト戦略、各種テストの実装方法、およびテストに関するガイドラインについて説明します。

*(現在執筆中です)*

## テスト戦略

*(テストピラミッド、各テストレイヤーの責務などを記載予定)*

## 単体テスト (Unit Tests)

*   **対象:** Serviceクラス、Domain Model、ユーティリティクラスなど、依存関係が少ないコンポーネント。
*   **ツール:** JUnit 5, Mockito, AssertJ
*   **リアクティブコードのテスト:** `StepVerifier`
*(実装例、モック化の方法、アサーションの書き方などを記載予定)*

## 結合テスト (Integration Tests)

*   **対象:** Controller/Delegate層、Repository層、複数のコンポーネントやモジュールが連携するシナリオ。
*   **ツール/アノテーション:**
    *   `@SpringBootTest`: Spring Boot アプリケーションコンテキスト全体をロードしてテストを実行します。API エンドポイントのテストなどに使用します。
    *   `@DataMongoTest`: MongoDB との連携に特化したテストスライス。Repository 層のテストに使用します。
    *   `@IntegrationTest` (`com.example.ec_2024b_back.utils.IntegrationTest`): `@SpringBootTest` と `@ActiveProfiles("test")` を組み合わせたカスタムアノテーション。主に Controller 層のテストで使用されます。
    *   Testcontainers: Docker コンテナを使用してテスト用の MongoDB インスタンスを起動します (`TestMongoConfig` 参照)。これにより、実際の DB 環境に近い状態でテストを実行できます。
    *   WireMock: (もし外部 API 連携があれば) 外部 API をモック化するために使用します。
*   **テスト用プロファイル:** `application-test.properties` でテスト用の設定 (例: MongoDB 接続情報) を定義します。
*   **実装例:**
    *   **Controller 層:** `AuthenticationControllerIntegrationTest` では `@IntegrationTest` を使用し、`WebTestClient` を使って `/api/authentication/login` エンドPOINT へのリクエスト送信とレスポンス検証を行っています。
    *   **Repository 層:** `MongoUserRepositoryTest` では `@DataMongoTest` を使用し、Testcontainers で起動した MongoDB に対してデータの保存・検索・削除などのテストを行っています。
*(テストデータの準備方法 (例: `@BeforeEach` でのデータ投入) なども記載予定)*

## E2Eテスト (End-to-End Tests)

*(もし実施している場合、使用ツール、実行方法などを記載予定)*

## テスト実装ガイドライン

*   **命名規則:** テストクラス、テストメソッドの命名規則。
*   **構造:** Given-When-Then パターンを推奨します。テストメソッド内で Arrange (準備), Act (実行), Assert (検証) の各ステップを明確に分離します。
*   **カバレッジ:**
    *   テストカバレッジの計測には JaCoCo Gradle プラグインを使用します。レポートは `./gradlew test jacocoTestReport` を実行後、`build/reports/jacoco/test/html/index.html` で確認できます。
    *   GitHub Actions 上でのカバレッジレポート生成・コメント投稿には [Octocov](https://github.com/k1LoW/octocov) を使用しています。設定はプロジェクトルートの `.octocov.yml` で行われています。
    *   目標カバレッジはプロジェクトのポリシーに従いますが、重要なドメインロジックや複雑な処理については高いカバレッジを目指します。
*   **実行速度:**
    *   テストの実行速度に応じて、テストクラスまたはメソッドにカスタムアノテーション `@Fast` または `@Slow` (`com.example.ec_2024b_back.utils` パッケージ参照) を付与することを推奨します。
    *   CI 環境などで実行するテストを絞り込む際に利用できます。通常、単体テストは `@Fast`、結合テストは `@Slow` となることが多いです。
*(その他、テストデータ管理 (例: テストデータ生成ユーティリティ)、アサーションの粒度 (AssertJ の活用) などのガイドラインを記載予定)*
