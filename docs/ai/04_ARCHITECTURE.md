# 4. アーキテクチャガイド

このドキュメントでは、`ecsite-v2` プロジェクトのシステムアーキテクチャについて詳細に説明します。

*(現在執筆中です。`01_PROJECT_OVERVIEW.md` の「モジュール設計」「API設計」セクションも参照してください)*

## 全体像

*(システム構成図、リクエストフローなどを記載予定)*

## レイヤー構造

本プロジェクトでは、クリーンアーキテクチャやヘキサゴナルアーキテクチャの考え方を参考に、以下の主要なレイヤー構造を採用しています。

*   **Interfaces (Adapter) 層:**
    *   役割: 外部とのインターフェースを担当します。
    *   コンポーネント例:
        *   `interfaces/rest`: Spring WebFlux Controller (OpenAPI Generatorで生成されたAPIインターフェースを実装)
        *   `interfaces/event`: イベントリスナーなど (将来的な拡張用)
*   **Application 層:**
    *   役割: アプリケーション固有のビジネスルール、ユースケースを実装します。
    *   コンポーネント例:
        *   `application/usecase`: 各ユースケースに対応するクラス。Domain層のWorkflowを呼び出し、Interfaces層とのデータのやり取り（DTOマッピングなど）を行います。
*   **Domain 層:**
    *   役割: アプリケーションのコアとなるビジネスロジック、エンティティ、値オブジェクトを定義します。フレームワークやインフラストラクチャへの依存を最小限に抑えます。
    *   コンポーネント例:
        *   `domain/model`: エンティティ、値オブジェクト
        *   `domain/workflow`: ビジネスプロセス全体を表現するワークフロー。複数のStepを組み合わせて構成されます。
        *   `domain/step`: Workflowを構成する個々の処理ステップ。関数型インターフェース (`Function<Input, Try<Output>>` など) として定義され、Vavrを活用して処理結果やエラーを扱います。
*   **Infrastructure 層:**
    *   役割: データベースアクセス、外部API連携、セキュリティ実装など、技術的な詳細を担当します。
    *   コンポーネント例:
        *   `infrastructure/repository`: Domain層で定義されたリポジトリインターフェースの実装 (例: Spring Data MongoDB Reactive)
        *   `infrastructure/security`: JWT生成・検証ユーティリティ、Spring Security設定など
        *   `infrastructure/config`: 各種設定クラス (Bean定義など)

**基本方針:**

*   **依存性の方向:** Interfaces → Application → Domain ← Infrastructure
*   **関数型プログラミング (FP) の活用:** Domain層 (特にWorkflow/Step) や Application層 (Usecase) では、Vavrライブラリを活用し、不変性、副作用の分離、合成可能性を意識した関数型スタイルでの実装を推奨します。これにより、テスト容易性やコードの堅牢性を高めます。

## Spring Modulith (モジュール設計)

*(モジュール分割の意図、境界、連携方法などを記載予定)*

## リアクティブ (WebFlux & Reactor)

*(ノンブロッキング処理、主要オペレータ、エラーハンドリング、コンテキスト、スケジューラなどを記載予定)*

## API (OpenAPI & SpringDoc)

本プロジェクトでは、API定義の記述とコード生成に OpenAPI Specification (OAS) を利用し、ドキュメント生成とUI提供に SpringDoc を活用します。

*   **API定義:**
    *   APIの仕様は `docs/openapi/entry.yml` に OAS 3.1.0 形式で記述します。
    *   このファイルを Single Source of Truth (SSOT) とし、APIの変更はまずこのファイルを更新することから始めます。
*   **コード生成 (OpenAPI Generator):**
    *   ルートプロジェクトの `build.gradle` および `openapi.gradle` に定義された `openApiGenerate` タスク (`org.openapi.generator` プラグイン) を使用します。
    *   `./gradlew openApiGenerate` (または `./gradlew build` など、依存関係に含まれるタスク) を実行すると、`docs/openapi/entry.yml` から以下のコードが `build/generated` ディレクトリに自動生成されます。
        *   APIインターフェース (`com.example.ec_2024b_back.api` パッケージ): Spring WebFlux (Reactive) ベースのインターフェース (`@Controller` アノテーションは付与されない)。
        *   モデル (DTO) クラス (`com.example.ec_2024b_back.model` パッケージ): リクエスト/レスポンスで使用するデータ構造。
    *   生成されたソースコード (`build/generated/src/main/java`) は、ルートプロジェクトの `sourceSets` に追加され、コンパイル対象となります。
    *   **設定:**
        *   `generatorName = "spring"`
        *   `interfaceOnly = "true"`: APIインターフェースのみを生成します (実装はメインプロジェクトで行う)。
        *   `reactive = "true"`: WebFlux (Reactor) ベースのコードを生成します。
        *   `useSpringBoot3 = "true"`: Spring Boot 3 向けのコードを生成します。
        *   `delegatePattern = "true"`: Controllerの実装を容易にするためのDelegateパターンを使用します (後述)。
*   **Delegate パターン:**
    *   OpenAPI Generator は `ApiDelegate` というインターフェースを生成します。
    *   メインプロジェクトの Controller (`interfaces/rest` パッケージ) では、生成された API インターフェースではなく、この `ApiDelegate` インターフェースを実装します。
    *   これにより、Controller は API のシグネチャ定義から分離され、ビジネスロジック (Usecase の呼び出し) に集中できます。Spring はリクエストを適切に Delegate にルーティングします。
*   **SpringDoc:**
    *   `springdoc-openapi-starter-webflux-ui` 依存関係により、実行時に OpenAPI ドキュメント (JSON/YAML) と Swagger UI が自動的に生成・提供されます。
    *   デフォルトでは `/v3/api-docs` と `/swagger-ui.html` でアクセス可能です。
    *   Controller や DTO に `@Schema`, `@Operation` などのアノテーションを追加することで、生成されるドキュメントをより詳細にカスタマイズできます (OpenAPI Generator が基本的なアノテーションを付与します)。

## データアクセス (MongoDB Reactive)

*(ReactiveMongoRepository, ReactiveMongoTemplate, エンティティ設計などを記載予定)*

## 認証・認可 (Spring Security)

*(SecurityFilterChain, WebFilter, UserDetailsService, メソッドレベルセキュリティなどを記載予定)*

## Null安全性 (JSpecify)

*(`@NonNull`, `@Nullable` の適用ルールなどを記載予定)*
