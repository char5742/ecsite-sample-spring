# CLAUDE.local.md

このファイルは、このリポジトリでコードを扱う際にClaude Code (claude.ai/code)に指針を提供します。

## ビルドコマンド
- ビルド: `./gradlew build`
- アプリケーション実行: `./gradlew bootRun`
- 全テスト実行: `./gradlew test`
- 単一テスト実行: `./gradlew test --tests "com.example.ec_2024b_back.auth.domain.models.AuthenticationTest"`
- タグ指定テスト実行: `./gradlew test -PincludeTags="Fast"` または `-PexcludeTags="Slow"`
- フォーマットチェック: `./gradlew spotlessCheck`
- フォーマット適用: `./gradlew spotlessApply`

## コードスタイルガイドライン
- アーキテクチャ: DDDの原則に基づくオニオンアーキテクチャ（ドメイン、アプリケーション、インフラストラクチャの各層）
- フォーマット: SpotlessによるGoogle Java Format（gitフックにより自動適用）
- 命名規則: クラスはパスカルケース、メソッド・変数はキャメルケース、パッケージはスネークケース
- テスト命名: `should[期待される結果]_when[テスト条件]`
- リアクティブプログラミングをReactor（Mono/Flux）で実装
- Null安全性: JSpecifyアノテーション（@Nullable）とNullAwayによる静的解析を使用
- エラー処理: ドメイン例外はDomainExceptionクラスを継承
- テスト: @Fast、@Slow、@IntegrationTest、@DatabaseTest、@ApiTestでテストにタグ付け


# プロジェクトマニュアル
以下はプロジェクトのマニュアルを自動的にマージした内容です。元のドキュメントは .roo/rules ディレクトリに保存されています。


## README


## 読み方

1. **[01_PROJECT_OVERVIEW.md](./01_PROJECT_OVERVIEW.md)** - プロジェクト概要
2. **[02_ENVIRONMENT_SETUP.md](./02_ENVIRONMENT_SETUP.md)** - 環境構築
3. **[03_BUILD_RUN_DEBUG.md](./03_BUILD_RUN_DEBUG.md)** - ビルド・実行・デバッグ
4. **[08_WORKFLOW.md](./08_WORKFLOW.md)** - Git操作・PR・レビュー

## 詳細資料

- **[04_ARCHITECTURE.md](./04_ARCHITECTURE.md)** - アーキテクチャ設計
- **[05_MODULES/](./05_MODULES/)** - 各モジュール詳細
- **[06_CODING_STANDARDS.md](./06_CODING_STANDARDS.md)** - コーディング規約
- **[07_TESTING.md](./07_TESTING.md)** - テスト戦略
- **[09_DATABASE.md](./09_DATABASE.md)** - DBスキーマ
- **[11_API_USAGE.md](./11_API_USAGE.md)** - API利用方法
- **[12_GLOSSARY.md](./12_GLOSSARY.md)** - 用語集

## 設計原則

- **技術分離**: ドメイン層とインフラ層の厳格な分離
- **リポジトリパターン**: インターフェースはドメイン層、実装はインフラ層
- **不変値オブジェクト**: `Email`, `AccountId`などの値オブジェクトは不変設計
- **モジュール構成**: 
  - **auth**: 認証・認可
  - **share**: 共通コンポーネント
  - **userprofile**: ユーザー情報・住所管理
  - **product**: 商品・カテゴリ・在庫・プロモーション管理
  - **shopping**: カート・注文・決済管理
  - **logistics**: 配送管理・配送状態追跡
  - **sample**: 新規モジュール作成の参考実装

## AIアシスタント利用

`.roo/rules/instruction.md`に基本指示を記載。作業後はドキュメント更新を忘れずに。


## PROJECT_OVERVIEW


## 主な特徴
- Spring Boot 3.4.3ベースのECサイトバックエンド
- Java 23
- Spring Modulithによるモジュラー設計
- リアクティブアーキテクチャ (WebFlux)
- MongoDB (Reactive)によるデータストア
- Spring Security認証

## 構成
```
src/
├── main/
│   ├── java/
│   │   └── com/example/ec_2024b_back/
│   │       ├── Ec2024bBackApplication.java
│   │       ├── auth/        # 認証・認可
│   │       ├── share/       # 共通コンポーネント
│   │       └── userprofile/ # ユーザー情報
│   └── resources/
└── test/
```

## 技術スタック

### 主要ライブラリ
- Spring Boot 3.4.3 / Modulith / WebFlux
- MongoDB Reactive Driver
- Lombok 1.18.38
- JSpecify 1.0.0 (Null安全性)
- jMolecules 1.10.0 (DDD実装)
- Auth0 JWT 4.4.0

### 開発ツール
- Gradle
- Spotless (コード整形)
- ErrorProne/NullAway (静的解析)
- Lefthook (Gitフック)

## ビルド・実行
```bash
./gradlew bootRun  # 開発実行
./gradlew build    # ビルド
```


## ENVIRONMENT_SETUP


## 必須ツール

### 1. JDK 23
- **GraalVM JDK 23**推奨（他のOpenJDK 23ディストリビューションも可能）
- 環境変数: `JAVA_HOME`と`PATH`にbin追加
- 確認: `java --version`

### 2. Docker
- MongoDB実行用（任意）
- 代替: MongoDB Community Serverのローカルインストール
- 確認: `docker --version`

### 3. Git
- バージョン管理用
- 初期設定
  ```bash
  git config --global user.name "Your Name"
  git config --global user.email "you@example.com"
  ```

## セットアップ手順

### プロジェクト取得
```bash
git clone <リポジトリURL> ecsite-sample-spring
cd ecsite-sample-spring
```

### MongoDBセットアップ
```bash
# Docker使用（推奨）
docker run --name local-mongo -p 27017:27017 -v ecsite-mongo-data:/data/db -d mongo:latest

# 接続確認（任意）
docker exec -it local-mongo mongosh
```

### コードフォーマッター
- Spotlessによる自動フォーマット
- Lefthookによるコミット時自動適用
- 手動実行: `./gradlew spotlessApply`

### 動作確認
```bash
./gradlew build
```

成功したら環境構築完了！


## BUILD_RUN_DEBUG


## 主要Gradleコマンド

```bash
# ビルド - JAR作成、テスト実行
./gradlew build

# 開発サーバー起動 (http://localhost:8080)
./gradlew bootRun

# テスト実行のみ
./gradlew test

# フォーマットチェック
./gradlew spotlessCheck

# 自動フォーマット適用
./gradlew spotlessApply

# ビルド成果物クリーンアップ
./gradlew clean
```

## 実行オプション

### プロファイル指定

```bash
# 開発プロファイル
./gradlew bootRun -Dspring.profiles.active=dev

# JAR実行時
java -Dspring.profiles.active=dev -jar build/libs/ecsite-sample-spring-0.0.1-SNAPSHOT.jar
```

### 環境変数設定

```bash
# Linux/macOS
export MY_VARIABLE=value
./gradlew bootRun

# Windows (Command Prompt)
set MY_VARIABLE=value
./gradlew bootRun

# Windows (PowerShell)
$env:MY_VARIABLE="value"
./gradlew bootRun
```

## デバッグ

### デバッグモード起動

```bash
# 簡易デバッグ
./gradlew bootRun --debug-jvm

# 詳細設定（ポート5005）
./gradlew bootRun -Dorg.gradle.jvmargs="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

### リアクティブデバッグ

パイプラインの可視化にはログオペレーターを活用：
```java
// 特定箇所にシグナルログを挿入
.log()  // onNext, onError, onCompleteなどをコンソール出力
```

## ログ設定

ログレベル変更（`application.properties`または起動引数）：
```
logging.level.root=DEBUG  # すべてのロガー
logging.level.com.example.ec_2024b_back=DEBUG  # プロジェクト固有
```


## ARCHITECTURE


ECサイトバックエンドのアーキテクチャ構成。

## 設計原則

- **オニオンアーキテクチャ**: ドメイン中心設計
- **モジュラーモノリス**: Spring Modulithによる境界付けられたコンテキスト
- **イベント駆動**: ドメインイベントの活用
- **ファクトリーパターン**: オブジェクト生成の責務を専用のファクトリークラスに集約
- **値オブジェクト**: IDなどの識別子は不変の値オブジェクトとして実装
- **依存性の注入**: 外部依存は明示的にインジェクション（特にIdGeneratorやTimeProviderなどの共通サービス）
- **インターフェース抽象化**: システム時刻などの外部依存はインターフェースで抽象化し、テスト容易性を向上

## 層構造

```mermaid
graph TD
    A[Client] --> B[Infrastructure: API];
    B --> C[Application: Usecase/Workflow];
    C --> D[Domain: Models/Repositories];
    C --> E[Infrastructure: 実装];
    E --> F[Database];
```

### 各層の責務

- **Domain層（中心）**: モデル、リポジトリインターフェース、ドメインイベント
- **Application層**: ユースケース、ワークフロー、ステップの定義
- **Infrastructure層**: ハンドラ、リポジトリ実装、外部サービス連携

## モジュール構成と依存関係

```mermaid
flowchart TD
    auth --> share;
    userprofile --> share;
    userprofile --> auth;
    product --> share;
    shopping --> share;
    shopping --> auth;
    shopping --> product;
    logistics --> share;
    logistics --> shopping;
```

- **[auth](./05_MODULES/auth/README.md)**: 認証・アカウント管理
- **[userprofile](./05_MODULES/userprofile/README.md)**: ユーザー情報・住所管理
- **[product](./05_MODULES/product/README.md)**: 商品・カテゴリ・在庫・プロモーション管理
- **[shopping](./05_MODULES/shopping/README.md)**: カート・注文・決済管理
- **[logistics](./05_MODULES/logistics/README.md)**: 配送管理・配送状態追跡
- **[share](./05_MODULES/share/README.md)**: 共通ユーティリティ（基盤モジュール）

## 技術スタック

- **リアクティブ**: Spring WebFlux + Project Reactor（`Mono`/`Flux`）による非同期処理
- **セキュリティ**: JWTベース認証、`PasswordEncoder`によるパスワードハッシュ化


## CODING_STANDARDS


## 基本方針
- **可読性・保守性・一貫性・シンプルさ**を重視

## 命名規則
- **パッケージ**: 小文字スネークケース (`com.example.ec_2024b_back.auth.domain`)
- **クラス・インターフェース・列挙型**: アッパーキャメルケース (`Account`)
- **メソッド・変数**: ローワーキャメルケース (`findByEmail`)
- **定数**: アッパースネークケース (`DEFAULT_TIMEOUT`)
- **テスト**: `クラス名Test`, `shouldXxxWhenYyy`

## 自動フォーマット
- **Spotless**: コミット前に自動適用 (`./gradlew spotlessApply`)

## コメント・テキスト
- **JavaDoc**: 日本語で記述、目的と引数・戻り値を明記
- **エラーメッセージ**: 日本語で記述、必要な識別情報を含める
- **TODO**: `// TODO: 内容 担当者/Issue番号`

## Java実装ガイドライン

### 型・データ操作
- **var使用**: ローカル変数のみ、型が明白な場合
- **Stream API**: 積極的に活用、過度に複雑な場合はメソッド抽出
- **Optional**: 戻り値のみ、フィールド・引数には非推奨
- **Lombok**: ボイラープレート削減 (`@Getter`, `@RequiredArgsConstructor`等)
- **値オブジェクト**: IDなどの識別子は不変のrecordとして実装 (`record ProductId(UUID value)`)
- **ファクトリーパターン**: オブジェクト生成の責務は専用のファクトリークラスに委譲
- **時刻操作**: `LocalDateTime.now()`などの直接呼び出しは避け、`TimeProvider`のような抽象化インターフェースを使用
- **監査情報の分離**: 作成日時・更新日時などの監査情報は`AuditInfo`クラスに集約
- **エラー情報の分離**: エラーコード・エラーメッセージなどのエラー情報は専用のクラスに集約（例: `PaymentError`）
- **警告の抑制**: TooManyParametersなどの警告は必要に応じて`@SuppressWarnings`で抑制（ビルダーパターン導入前に検討）

### 設定・プロパティ
- **推奨**: `@ConfigurationProperties`による型安全なクラス
- **検証**: `@Validated`と`@NotNull`等で起動時検証

### 静的解析・Null安全性

#### NullAway
- パッケージレベルで`@NullMarked`適用
- Null可能性は`@Nullable`で明示
- 外部データ境界では`@NullUnmarked`可

#### パッケージ構造 (auth/userprofile)
```
module/
├── api/                  # APIエンドポイント
├── application/          # アプリケーション層
│   └── workflow/         # ワークフロー実装
├── domain/               # ドメイン層
│   ├── models/           # ドメインモデル
│   └── repositories/     # リポジトリIF
└── infrastructure/       # インフラ層
    └── repository/       # リポジトリ実装
```


## TESTING


このドキュメントでは、`ecsite-v2` プロジェクトにおけるテスト戦略、各種テストの実装方法、およびテストに関するガイドラインについて説明します。

## テスト戦略

本プロジェクトでは、一般的に「テストピラミッド」と呼ばれる考え方に基づき、以下の階層でテストを構成します。

1. **単体テスト (Unit Tests):** 最も数が多く、高速に実行できるテスト。個々のクラスやメソッドのロジックを検証します。外部依存はモック化します。
2. **結合テスト (Integration Tests):** 複数のコンポーネントやモジュール、または外部システム (DBなど) との連携を検証するテスト。単体テストより数は少なく、実行に時間がかかります。
3. **E2Eテスト (End-to-End Tests):** (現在は実施していません) 実際のユーザー操作を模倣し、システム全体の動作を検証するテスト。最も実行コストが高いです。

## 単体テスト (Unit Tests)

**対象:**
- **ドメインモデル** (`Account`, `EmailAuthentication` など): 不変性、バリデーション、ビジネスロジックの検証
- **Workflow** (`LoginWorkflow` など): 依存するStepをモック化し、ステップ呼び出し順序やロジックフローを検証
- **Usecase** (`LoginUsecase` など): 依存するWorkflowやRepositoryをモック化し、アプリケーション層のロジックを検証
- **Stepインターフェース**: 振る舞いを定義通りに呼び出すかの検証
- **ファクトリークラス** (`ProductFactory`, `CategoryFactory` など): IdGeneratorをモック化し、生成したオブジェクトの属性を検証
- **ユーティリティクラス**

**ツール:** JUnit 5, Mockito (モック化), AssertJ (アサーション)

**リアクティブコードのテスト:** `StepVerifier`を使用して`Mono`や`Flux`の発行するシグナルを検証

**実装例:**
- `src/test/java/com/example/ec_2024b_back/auth/domain/models/EmailAuthenticationTest.java`
- `src/test/java/com/example/ec_2024b_back/auth/domain/workflow/LoginWorkflowTest.java`
- `src/test/java/com/example/ec_2024b_back/auth/application/usecase/LoginUsecaseTest.java`

## 結合テスト (Integration Tests)

**対象:**
- **Repository実装** (`MongoAccounts`など): 実際のDB (Testcontainersで起動) との連携を検証
- **Step実装** (`FindAccountByEmailStepImpl`など): DBや他のインフラコンポーネントとの連携を検証
- **APIハンドラー/コントローラー層**: リクエスト受付からレスポンス返却までの一連の流れを検証

**ツール/アノテーション:**
- `@SpringBootTest`: Spring Boot アプリケーションコンテキスト全体をロードしてテスト実行
- `@DataMongoTest`: MongoDB との連携に特化したテストスライス
- `@IntegrationTest` (`com.example.ec_2024b_back.utils.IntegrationTest`): `@SpringBootTest`と`@ActiveProfiles("test")`の組み合わせ
- **Testcontainers**: Dockerコンテナで一時的なMongoDB環境を提供
- `WebTestClient`: リアクティブなAPIエンドポイントのテスト用クライアント

**テスト用プロファイル:** 現在`src/test/resources`に`application-test.properties`は存在しません。テスト固有の設定が必要な場合は、Testcontainersの設定やテストクラス内でのプロパティ設定で行います。

## テストタグ

テストには適切なタグを付けることで実行時の選別や実行環境の区別を容易にします。必ず適切なタグを付けてください。

### 用意されているタグ

- **@Fast**: 実行時間が短く外部依存がない単体テスト（CIパイプラインでは常に実行）
- **@Slow**: 実行時間が長いテストや外部リソースに依存するテスト
- **@IntegrationTest**: 複数コンポーネントの統合テスト
- **@DatabaseTest**: データベースとの結合テスト
- **@ApiTest**: APIエンドポイントのテスト

### タグの使用方法

```java
import com.example.ec_2024b_back.utils.Fast;
import org.junit.jupiter.api.Test;

@Fast
class FastUnitTest {
    @Test
    void someTest() {
        // テスト実装
    }
}
```

クラスレベルでタグを適用すると、そのクラス内のすべてのテストメソッドにタグが適用されます。メソッドレベルでタグを適用することも可能です。

### タグ付けのガイドライン

- **すべてのテストに少なくとも1つのタグを付ける**: テストはその特性に応じて適切にタグ付けする
- **複数のタグの併用**: テストの性質に応じて複数のタグを適用可能
- **新しいタグの追加**: 必要に応じて新しいタグを追加する場合はチームでの合意が必要

## テスト実行方法

```bash
# 全テスト実行
./gradlew test

# 特定タグのテスト実行
./gradlew test -PincludeTags="Fast"

# 特定タグのテスト除外
./gradlew test -PexcludeTags="Slow"

# 複数タグ指定 (OR条件)
./gradlew test -PincludeTags="Fast | IntegrationTest"

# カバレッジレポート生成
./gradlew test jacocoTestReport
```

## テスト実装ガイドライン

### 命名規則
- **テストクラス**: `<対象クラス>Test` (例: `LoginWorkflowTest`)
- **テストメソッド**: `should<期待される結果>_when<テスト条件>` (例: `shouldReturnTokenWhenCredentialsAreValid`)
  - 日本語での記述も可。その場合は一貫性をもたせて使用すること。

### テスト構造
- **Arrange-Act-Assert パターン** (または Given-When-Then) を使用
  - **Arrange**: テストの準備（データ作成、モック設定等）
  - **Act**: テスト対象メソッド実行
  - **Assert**: 結果検証
- テストメソッド内で準備、実行、検証の各ステップを明確に分離（コメントや空行で区切る）

### カバレッジ
- **測定ツール**: JaCoCo Gradleプラグイン 
  - レポートは`./gradlew test jacocoTestReport`実行後`build/reports/jacoco/test/html/index.html`で確認
- **CI連携**: GitHub Actions上でカバレッジレポート生成・コメント投稿に[Octocov](https://github.com/k1LoW/octocov)を使用
- **目標値**: プロジェクトポリシーに従うが、重要なドメインロジックや複雑な処理は高カバレッジを目指す

### モック利用例
```java
@ExtendWith(MockitoExtension.class)
class ExampleTest {
    @Mock
    private ExternalDependency dependency;
    
    @InjectMocks
    private ServiceUnderTest service;
    
    @Test
    void test() {
        // Arrange
        when(dependency.method()).thenReturn(expectedValue);
        
        // Act
        Result result = service.methodUnderTest();
        
        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }
}
```

### ファクトリーパターンのテスト例
```java
@ExtendWith(MockitoExtension.class)
class ProductFactoryTest {
    @Mock
    private IdGenerator idGenerator;
    
    @InjectMocks
    private ProductFactory productFactory;
    
    @Test
    void shouldCreateProduct_withGeneratedId() {
        // Arrange
        var uuid = UUID.randomUUID();
        when(idGenerator.newId()).thenReturn(uuid);
        
        var name = "テスト商品";
        var description = "テスト商品の説明";
        
        // Act
        var product = productFactory.create(name, description, ...);
        
        // Assert
        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(new ProductId(uuid));
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getDescription()).isEqualTo(description);
    }
}
```

### リアクティブテスト例
```java
@Test
void reactiveTest() {
    // Arrange
    Mono<String> mono = service.getReactiveMono();
    
    // Act & Assert
    StepVerifier.create(mono)
        .expectNext("expected value")
        .verifyComplete();
}
```

### その他の推奨事項
- **テストデータ**: 各テストメソッド内で準備するか`@BeforeEach`を使用。テストデータ生成ユーティリティの作成も検討。
- **アサーション**: AssertJを活用し、流暢で読みやすい検証コードを記述。
- **テスト分離**: テスト間で依存関係を持たせない。各テストが独立して実行できることを確保。
- **境界値テスト**: エッジケースやバリデーション境界値のテストを積極的に作成。
- **実行速度考慮**: テストの実行速度に応じて`@Fast`または`@Slow`タグを適切に付与。


## WORKFLOW


このドキュメントでは、`ecsite-v2` プロジェクトにおける標準的な開発ワークフローについて説明します。一貫したプロセスに従うことで、コードの品質を維持し、チームでの共同作業を円滑に進めることができます。

## Git Flow

当プロジェクトでは、Git Flowに基づいたブランチ戦略を採用しています。

### 主要ブランチ

| ブランチ | 説明 | 役割と制約 |
|---------|------|------------|
| `main` | リリース可能な安定版 | • 直接コミット禁止<br>• `release`または`hotfix`ブランチからのみマージ可能<br>• 常に本番環境にデプロイ可能な状態を維持 |
| `develop` | 開発統合用 | • 次期リリースに向けた開発の統合<br>• `feature`ブランチからのマージを受け入れ<br>• `main`ほど安定していないが、基本的な動作は保証 |

### サポートブランチ

| ブランチ | 発生元 | マージ先 | 命名規則 | 用途 |
|---------|-------|---------|---------|-------|
| `feature/*` | `develop` | `develop` | `feature/issue-<番号>-<説明>` | 新機能開発、既存機能改善、リファクタリングなど |
| `release/*` | `develop` | `main` + `develop` | `release/v<バージョン>` | リリース前の最終調整、軽微なバグ修正、ドキュメント更新 |
| `hotfix/*` | `main` | `main` + `develop` | `hotfix/<説明>` | 本番環境の緊急バグ修正 |

### 基本的な開発フロー (新機能開発)

1. **最新のdevelopブランチを取得:**
   ```bash
   git checkout develop
   git pull origin develop
   ```

2. **featureブランチを作成:**
   ```bash
   git checkout -b feature/issue-XXX-your-feature-name develop
   ```

3. **コーディングとコミット:**
   * 機能開発を進め、適切な粒度でコミット
   * コミットメッセージは後述の規約に従う
   ```bash
   git add .
   git commit -m "feat: 🎸 ユーザープロファイル編集機能を追加"
   ```

4. **リモートへのプッシュ:**
   ```bash
   git push origin feature/issue-XXX-your-feature-name
   ```

5. **Pull Request (PR) の作成:**
   * GitHubで`feature/issue-XXX`から`develop`へのPRを作成
   * テンプレートに従い、変更内容、理由、テスト内容などを記述

6. **コードレビュー:**
   * レビュワーを指名し、レビューを依頼
   * レビューでの指摘事項に対応し、必要に応じて追加コミット

7. **PRのマージ:**
   * レビュー承認後、`develop`ブランチにマージ
   * 通常はSquash and mergeは使用せず、履歴を保持

8. **ローカルブランチの削除 (任意):**
   ```bash
   git checkout develop
   git branch -d feature/issue-XXX-your-feature-name
   ```

## コミット前の自動チェック (Lefthook)

当プロジェクトでは、コミット前にコードの品質を自動的にチェック・整形するために [Lefthook](https://github.com/evilmartians/lefthook) を使用しています。

* **設定ファイル:** プロジェクトルートの `lefthook.yml`
* **pre-commitフック:** コミット前に以下の処理が自動実行
  * **Javaコード整形:** Spotless Gradleプラグイン (`./gradlew spotlessApply`) によるフォーマット
* **動作:** `git commit`実行時に自動的に実行され、整形によりファイルが変更された場合は再度ステージングが必要
  ```bash
  # 整形によりファイルが変更された場合
  git add .
  git commit --amend --no-edit  # または再度コミットメッセージを入力
  ```
* **スキップ (非推奨):** 一時的にフックをスキップする場合は `--no-verify` オプションを使用
  ```bash
  git commit -m "..." --no-verify
  ```

## コミットメッセージ規約

当プロジェクトでは、以下の形式に基づいたコミットメッセージを使用します。この規約は効果的な履歴管理とリリースノート自動生成のために重要です。

**フォーマット:**
```
{type}: {emoji}{subject}

[optional body]
```

### タイプと絵文字

| タイプ | 説明 | 絵文字 | 使用例 |
|--------|------|--------|--------|
| `feat` | ユーザー向けの新機能実装 | 🎸 | `feat: 🎸 パスワードリセット機能を追加` |
| `fix` | バグ修正 | 🐛 | `fix: 🐛 ログイン時のNullPointerExceptionを修正` |
| `docs` | ドキュメント更新 | ✏️ | `docs: ✏️ READMEのセットアップ手順を更新` |
| `style` | コードスタイル変更 (機能への影響なし) | 💄 | `style: 💄 インデントを修正` |
| `refactor` | 機能変更のないコード改善 | 💡 | `refactor: 💡 認証処理をリファクタリング` |
| `perf` | パフォーマンス改善 | ⚡️ | `perf: ⚡️ データベースクエリを最適化` |
| `test` | テスト追加・修正 | 💍 | `test: 💍 ユーザー登録APIのテストケースを追加` |
| `build` | ビルドシステム・依存関係変更 | 🏗️ | `build: 🏗️ Spring Bootを3.1.2に更新` |
| `ci` | CI設定変更 | 🎡 | `ci: 🎡 GitHub Actionsのワークフローを改善` |
| `chore` | その他の変更 | 🤖 | `chore: 🤖 .gitignoreを更新` |
| `release` | リリース関連 | 🏹 | `release: 🏹 v1.0.0をリリース` |

### コミットメッセージの詳細

* **タイプ (type):** 変更の種類を示すキーワード (必須)。上表のいずれかを使用。
* **絵文字 (emoji):** 変更の種類を視覚的に示す (必須)。上表のいずれかを使用。
* **件名 (subject):** 変更内容の簡潔な説明 (必須)
  * 64文字以内を推奨
  * 現在形・命令形で記述
  * 大文字で始めず、ピリオドで終えない
* **本文 (body):** 変更の詳細な説明 (任意)
  * 変更の理由、背景、以前の動作との違いなど
  * 空行を挟んで記述

### コミットメッセージ例

```
feat: 🎸 ユーザープロファイル編集機能を追加

- プロフィール更新用のAPIエンドポイントを実装
- 関連するサービスとリポジトリロジックを追加
- ユーザーモジュールのドキュメントを更新

Refs #123
```

```
fix: 🐛 ログイン時のNullPointerExceptionを修正

特定の条件下でアカウント情報が取得できずにエラーが発生していた問題を修正。
リポジトリ層でのOptionalの扱いを見直し。
```

## Pull Request (PR)

PRは、コード変更をレビューし、`develop`や`main`ブランチにマージするための主要な手段です。

### PRテンプレート項目

* **変更の概要:** 何を変更したのか簡潔に
* **変更の理由/目的:** 背景や解決しようとした問題
* **関連Issue:** 関連するIssueへのリンク (`Fixes #XXX`, `Refs #XXX`)
* **変更内容の詳細:** 実装方法や技術的な判断など
* **テスト内容:** 単体テスト、結合テスト、手動確認など
* **レビュー依頼事項:** 特に見てほしい点、懸念点など
* **スクリーンショット/動画:** UI変更がある場合に添付

### WIP (Work In Progress) PR

開発途中のコードで早期フィードバックが欲しい場合に使用:
* タイトルに`[WIP]`をつけてPRを作成
* マージ準備が整うまでマージ不可のまま議論を進める
* 準備が整ったら`[WIP]`を外し、最終レビューを依頼

## コードレビュー

### レビュー観点

* **設計:** 要件充足、拡張性、保守性、DDDの原則適用
* **ロジック:** 動作正確性、エッジケース対応、リアクティブパターン適用
* **可読性:** 理解しやすさ、命名適切性、コメントの質
* **テスト:** カバレッジ、テスト品質、単体/結合テストの適切な使い分け
* **規約:** コーディング規約準拠、プロジェクト標準の適用
* **パフォーマンス:** 効率性、リソース使用、リアクティブストリーム適用
* **セキュリティ:** 認証・認可の取り扱い、入力検証、トークン管理

### レビュープロセス

1. PRに割り当てられたらできるだけ2営業日以内にレビューする
2. 具体的かつ建設的なコメントを心がける（単なる問題点指摘ではなく改善案も）
3. 敬意を持ったコミュニケーションを取る
4. LGTM (Looks Good To Me) だけでなく、良いと思った理由も伝える

### レビュー後のフロー

1. 指摘事項に対応し、必要に応じて追加コミット
2. 変更点を説明するコメントを残す
3. PRのマージには、少なくとも1人のチームメンバーの承認が必要

## CI/CD

現在、GitHub Actionsなどを用いたCI/CDパイプラインは設定されていませんが、将来的には以下の導入を検討しています:

* **CI:** PR作成時やブランチへのプッシュ時の自動ビルド・テスト実行
* **静的解析:** Spotless, NullAway, Error Proneによるコード品質チェック
* **テスト実行:** JUnit, Testcontainersを使った自動テスト
* **CD:** 承認済みPRのdevelopブランチへのマージ後、開発環境への自動デプロイ


## DATABASE


MongoDB（ドキュメント指向NoSQL）を採用。リアクティブドライバーで非同期アクセスを実現。

## コレクション

- **`accounts`**: アカウント情報と認証情報
- **`user_profiles`**: ユーザープロファイルと住所情報
- **`products`**: 商品情報
- **`categories`**: カテゴリ情報
- **`inventories`**: 在庫情報
- **`promotions`**: プロモーション情報
- **`carts`**: ショッピングカート情報
- **`orders`**: 注文情報
- **`payments`**: 支払い情報
- **`shipments`**: 配送情報

## スキーマ詳細

### `accounts` コレクション

アカウントと認証情報を保存します。

```mermaid
erDiagram
    accounts {
        String _id PK "アカウントID"
        List~AuthenticationDocument~ authentications "認証情報リスト"
    }
    accounts ||--o{ authentications : contains
```

**推奨インデックス:** `authentications.email`

### `user_profiles` コレクション

ユーザープロファイルと住所情報を保存します。

```mermaid
erDiagram
    user_profiles {
        String _id PK "プロファイルID"
        String name "ユーザー名"
        String accountId FK "アカウントID"
        List~AddressDocument~ addresses "住所リスト"
    }
    user_profiles ||--o{ addresses : contains
```

**推奨インデックス:** `accountId`

### `products` コレクション

商品情報を保存します。

```mermaid
erDiagram
    products {
        String _id PK "商品ID"
        String name "商品名"
        String description "商品説明"
        Number price "価格"
        String categoryId FK "カテゴリID"
        List~String~ images "画像URL"
    }
```

**推奨インデックス:** `categoryId`, `name`

### `categories` コレクション

商品カテゴリ情報を保存します。

```mermaid
erDiagram
    categories {
        String _id PK "カテゴリID"
        String name "カテゴリ名"
        String description "カテゴリ説明"
        String parentId FK "親カテゴリID（任意）"
    }
```

**推奨インデックス:** `parentId`, `name`

### `inventories` コレクション

在庫情報を保存します。

```mermaid
erDiagram
    inventories {
        String _id PK "在庫ID"
        String productId FK "商品ID"
        Number quantity "数量"
        String status "ステータス（在庫あり/なし等）"
    }
```

**推奨インデックス:** `productId`

### `promotions` コレクション

プロモーション情報を保存します。

```mermaid
erDiagram
    promotions {
        String _id PK "プロモーションID"
        String name "プロモーション名"
        String description "説明"
        String type "割引タイプ（額/率/etc）"
        Number value "割引値"
        Date startDate "開始日"
        Date endDate "終了日"
        List~String~ productIds "対象商品ID"
        List~String~ categoryIds "対象カテゴリID"
    }
```

**推奨インデックス:** `startDate`, `endDate`

### `carts` コレクション

ショッピングカート情報を保存します。

```mermaid
erDiagram
    carts {
        String _id PK "カートID"
        String accountId FK "アカウントID"
        List~CartItemDocument~ items "カート内アイテム"
        Date createdAt "作成日時"
        Date updatedAt "更新日時"
    }
    carts ||--o{ cart_items : contains
```

**推奨インデックス:** `accountId`

### `orders` コレクション

注文情報を保存します。

```mermaid
erDiagram
    orders {
        String _id PK "注文ID"
        String accountId FK "アカウントID"
        List~OrderItemDocument~ items "注文アイテム"
        Number subtotal "小計"
        Number tax "税額"
        Number shippingCost "配送料"
        Number totalAmount "合計金額"
        String status "注文状態"
        String shippingAddress "配送先住所"
        String trackingNumber "追跡番号（任意）"
        String paymentId FK "支払いID（任意）"
        String paymentMethod "支払い方法（任意）"
        Date createdAt "作成日時"
        Date updatedAt "更新日時" 
    }
    orders ||--o{ order_items : contains
```

**推奨インデックス:** `accountId`, `status`, `createdAt`

### `payments` コレクション

支払い情報を保存します。

```mermaid
erDiagram
    payments {
        String _id PK "支払いID"
        String orderId FK "注文ID"
        Number amount "支払い金額"
        String status "支払い状態"
        String method "支払い方法"
        String transactionId "外部決済システムトランザクションID（任意）"
        String errorReason "エラー理由（任意）"
        Date createdAt "作成日時"
        Date updatedAt "更新日時"
    }
```

**推奨インデックス:** `orderId`, `status`

### `shipments` コレクション

配送情報を保存します。

```mermaid
erDiagram
    shipments {
        String _id PK "配送ID"
        String orderId FK "注文ID"
        String shippingAddress "配送先住所"
        String shippingMethod "配送方法"
        String status "配送状態"
        String trackingNumber "追跡番号（任意）"
        String note "備考（任意）"
        Date estimatedDeliveryDate "配送予定日時（任意）"
        Date actualDeliveryDate "実際の配送完了日時（任意）"
        String receiverName "受取人名（任意）"
        Date createdAt "作成日時"
        Date updatedAt "更新日時"
    }
```

**推奨インデックス:** `orderId`, `status`, `trackingNumber`

## 設計ノート

- **埋め込みパターン:** 関連データを単一ドキュメント内に埋め込み
- **マイグレーション:** 専用ツール未導入。スキーマ変更は手動対応
- **イベントソーシング:** ドメインイベントを保存する設計（現在は実装なし）



## TROUBLESHOOTING


このドキュメントでは、開発中に遭遇する可能性のある一般的な問題とその解決策、および問題発生時の調査方法について説明します。

## 問題切り分けの基本手順

問題が発生した場合、以下の手順で切り分けることが有効です。

1. **エラーメッセージ確認:**
   * コンソールやログに出力されたエラーメッセージを正確に読む
   * 特に例外の種類 (`NullPointerException`, `IllegalArgumentException` など) とスタックトレースに注目

2. **スタックトレース解析:**
   * エラーが発生するまでのメソッド呼び出し履歴を示す
   * 一番上の行が直接的なエラー発生箇所
   * 呼び出し元を辿ることで、エラーの原因となったコード箇所を特定

3. **再現手順特定:**
   * どのような操作や条件で問題が必ず発生するかを特定
   * これによりデバッグや原因調査が効率化される

4. **ログレベル変更:**
   * 必要に応じてログレベルを `DEBUG` や `TRACE` に変更して詳細情報を取得
   * `application.properties` の `logging.level.*` プロパティで設定
   * 詳細は [03_BUILD_RUN_DEBUG.md](./03_BUILD_RUN_DEBUG.md) を参照

5. **デバッガの利用:**
   * デバッガを使ってコードをステップ実行し、変数の値や処理の流れを確認
   * 詳細は [03_BUILD_RUN_DEBUG.md](./03_BUILD_RUN_DEBUG.md) を参照

## よくある問題と解決策

### 環境構築時のエラー

| 問題 | 原因 | 解決策 |
|------|------|---------|
| **JDK関連エラー** | • JDKバージョン不一致 (JDK 23 以外の使用)<br>• `JAVA_HOME` が未設定または誤っている<br>• IDEのJDK設定が誤っている | • [02_ENVIRONMENT_SETUP.md](./02_ENVIRONMENT_SETUP.md) に従い、正しい JDK をインストール<br>• 環境変数を正しく設定<br>• IDEの設定を確認 |
| **Docker関連エラー** | • Dockerが未起動<br>• MongoDBコンテナ起動失敗<br>• ポート競合 (27017ポート)<br>• リソース不足 | • Docker Desktop起動<br>• `docker ps`でコンテナ確認<br>• `docker logs local-mongo`でログ確認<br>• 別ポートで再起動 (`docker run ... -p <別ポート>:27017 ...`)<br>• Docker設定でリソース割り当て確認 |
| **Gradle関連エラー** | • 依存関係ダウンロード失敗<br>• キャッシュ破損<br>• ネットワーク制限<br>• Gradle Wrapperの破損 | • ネットワーク接続確認<br>• `./gradlew clean build --refresh-dependencies`<br>• プロキシ設定確認<br>• 必要に応じてGradle Wrapperを再生成 |

### ビルド/コンパイルエラー

| 問題 | 原因 | 解決策 |
|------|------|---------|
| **コンパイルエラー** | • 文法ミス<br>• 型不一致<br>• import忘れ<br>• 依存関係不足 | • IDEのエラー表示確認<br>• コンパイラメッセージに従い修正<br>• 必要なライブラリを`build.gradle`に追加 |
| **NullAwayエラー** | • `@Nullable`でない変数に`null`が代入される可能性<br>• `@Nullable`な変数をチェックせずに使用<br>• `@NullMarked`/`@NullUnmarked`の使い分け誤り | • [06_CODING_STANDARDS.md](./06_CODING_STANDARDS.md) のNullAwayセクション参照<br>• 適切な`@Nullable`アノテーション適用<br>• nullチェック追加<br>• `Optional`活用 |
| **テスト失敗** | • コード変更によるテスト前提条件の崩れ<br>• アサーションエラー<br>• テスト内の実行時例外<br>• モックの設定ミス | • `build/reports/tests/test/index.html`でテスト結果確認<br>• テストコードまたは本体コードを修正<br>• モックの振る舞いを正しく設定 |

### 実行時エラー

| 問題 | 原因 | 解決策 |
|------|------|---------|
| **NullPointerException** | • nullオブジェクトのメソッド/フィールドアクセス<br>• NullAwayが検出できないケース | • スタックトレースで発生箇所特定<br>• nullチェック追加<br>• `Optional`活用<br>• 防御的プログラミングの適用 |
| **ClassCastException** | • 互換性のない型へのキャスト<br>• 型検査の欠如 | • キャスト前に`instanceof`で型検査<br>• コードロジック見直し<br>• ジェネリクス活用 |
| **MongoDB接続エラー** | • コンテナ未起動<br>• 接続設定不正<br>• ネットワーク問題 | • コンテナ起動確認<br>• `application.properties`の`spring.data.mongodb.uri`確認<br>• ネットワーク接続確認 |
| **リアクティブ関連エラー** | • デッドロック<br>• バックプレッシャー問題<br>• リソースリーク<br>• ブロッキング処理 | • Reactorの`log()`オペレータ使用<br>• デバッガで処理フロー確認<br>• ブロッキング処理排除<br>• `subscribeOn`/`publishOn`の適切な使用 |

### APIエラー

| ステータス | 原因 | 解決策 |
|----------|------|---------|
| **4xx (クライアントエラー)** | • リクエスト形式不正<br>• 必須パラメータ欠落<br>• 型違い<br>• トークン無効/未提供 | • API仕様 (Swagger UI) 確認<br>• リクエスト内容修正<br>• 認証トークン確認<br>• デバッグログでリクエスト内容検証 |
| **5xx (サーバーエラー)** | • サーバー内部例外<br>• 未処理例外のスロー<br>• DB接続問題<br>• リソース枯渇 | • サーバーログ確認<br>• スタックトレース解析<br>• 例外処理の追加<br>• エラーハンドリング改善 |

## 特定シナリオの対処方法

### MongoDBインデックス問題

MongoDBの検索パフォーマンスが遅い場合：

1. **問題確認:**
   ```bash
   # コンテナ内のMongoシェル起動
   docker exec -it local-mongo mongosh
   
   # 使用中のDB選択
   use test
   
   # クエリ実行計画確認
   db.accounts.find({email: "user@example.com"}).explain("executionStats")
   ```

2. **インデックス作成:**
   ```javascript
   // メールアドレスでのインデックス作成例
   db.accounts.createIndex({"authentications.email": 1}, {unique: true})
   ```

### リアクティブストリームのデバッグ

リアクティブプログラミングのデバッグには以下のテクニックが有効です：

1. **`log()`オペレータの挿入:**
   ```java
   return accountRepository.findByEmail(email)
       .log("FIND_RESULT") // 処理の各ステップでシグナルをログ出力
       .flatMap(this::processAccount)
       .log("PROCESS_RESULT");
   ```

2. **デバッグレベルの有効化:**
   ```properties
   # application.properties
   logging.level.reactor.core.publisher.Operators=DEBUG
   logging.level.reactor=DEBUG
   ```

3. **ブロッキング呼び出しの特定:**
   BlockHoundなどのツールを使用（現在未導入）

## 調査ツール

### ログ確認

* **コンソールログ:** デフォルトではコンソールに出力されます
* **ファイルログ設定:**
  ```properties
  # application.properties
  logging.file.name=application.log
  logging.file.path=/path/to/logs
  ```
* **ログレベル変更:**
  ```properties
  # 全体のログレベル
  logging.level.root=INFO
  
  # 特定パッケージのログレベル
  logging.level.com.example.ec_2024b_back=DEBUG
  logging.level.com.example.ec_2024b_back.auth=TRACE
  ```

### デバッガ活用

* **IDEデバッガ:** IntelliJ IDEAなどのJavaデバッガを使用
* **リモートデバッグ:**
  ```bash
  ./gradlew bootRun --debug-jvm
  ```
* **条件付きブレークポイント:** 特定条件下でのみ停止するようブレークポイントを設定
* **ウォッチ式:** 変数や式の値を監視
* **実行制御:** ステップイン、ステップオーバー、ステップアウト機能を活用

### その他の調査ツール

* **Spring Boot Actuator:** 現在非導入ですが、導入することでヘルスチェック、メトリクスなどの監視が可能になります
* **プロファイラ:** VisualVM、JProfilerなどのJavaプロファイラを使用してパフォーマンス問題やメモリリークを調査

## 質問テンプレート

問題が解決しない場合は、チームに質問するときに以下のテンプレートを使用してください。具体的な情報を提供することで、より適切な回答が得られます：

```markdown
## 問題
[問題の簡潔な説明]

## エラーメッセージ/ログ
```
[エラーメッセージや関連するログを貼り付け]
```

## 再現手順
1. [手順1]
2. [手順2]
3. [手順3]

## 試したこと
* [調査1]
* [調査2]

## 期待する結果
[本来期待される動作]

## 環境情報
* OS: [OS名/バージョン]
* JDK: [JDKバージョン]
* Docker: [Dockerバージョン]
* IDE: [IDE名/バージョン]
```


## API_USAGE


このドキュメントでは、`ecsite-v2` プロジェクトが提供するAPIの利用方法について説明します。主に開発中の動作確認やテストを目的としています。

## OpenAPI仕様

本プロジェクトでは、OpenAPIに準拠したREST APIを提供しています。API仕様はOpenAPI形式で記述され、`docs/openapi/entry.yml`で確認できます。この仕様は常に最新の状態に保たれるよう管理されています。

## Swagger UI

Swagger UIは、APIをインタラクティブに操作・テストするためのツールです。デフォルトでは無効化されていますが、以下の手順で有効化できます。

### Swagger UI有効化方法

1. `application.properties`に以下の設定を追加します：
   ```properties
   springdoc.swagger-ui.enabled=true
   ```

2. アプリケーションを起動します：
   ```bash
   ./gradlew bootRun
   ```

3. ブラウザで以下のURLにアクセスします：
   ```
   http://localhost:8080/swagger-ui.html
   ```
   (ポート番号は環境によって異なる場合があります)

### Swagger UI使用方法

1. **API一覧確認**: 画面上部にAPI一覧が表示されます。
2. **API詳細確認**: 各APIをクリックすると詳細（説明、パラメータ、リクエストボディ、レスポンススキーマ）が展開されます。
3. **API試行手順**:
   1. 試したいAPIを展開します
   2. 右上の "Try it out" ボタンをクリックします
   3. 必要なパラメータやリクエストボディを入力します
   4. "Execute" ボタンをクリックしてリクエストを送信します
   5. レスポンス（ステータスコード、レスポンスボディ、ヘッダー、curlコマンド例）が表示されます

## 認証方法

本プロジェクトのAPIは、多くのエンドポイントで認証（JWT）が必要です。以下で認証の流れを説明します。

### 1. トークン取得 (ログインAPI)

認証が必要なAPIにアクセスする前に、まずはログインAPIでJWTトークンを取得する必要があります。

```bash
curl -X POST "http://localhost:8080/api/authentication/login" \
 -H "accept: application/json" \
 -H "Content-Type: application/json" \
 -d '{
  "email": "user@example.com",
  "password": "password"
}'
```

成功すると、以下のような形式でJWTトークンが返却されます：

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzEzMDE4MjAwLCJleHAiOjE3MTMwMjE4MDB9.xxxxxxxxxxxxxxxxxxxxxxxxxxxx"
}
```

失敗時（認証情報が間違っている場合など）は、401 Unauthorizedエラーが返却されます：

```json
{
  "timestamp": "...",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication failed",
  "path": "/api/authentication/login"
}
```

### 2. トークンの使用方法

取得したJWTトークンを使って認証が必要なAPIにアクセスする方法は以下の通りです。

#### Swagger UIでのトークン設定

1. Swagger UI右上の "Authorize" ボタンをクリックします
2. 表示されたダイアログの "Value" フィールドに、取得したJWTトークンを `Bearer <token>` の形式で入力します
   ```
   Bearer eyJhbGciOiJIUzI1NiJ9...
   ```
3. "Authorize" ボタンをクリックしてダイアログを閉じます
4. これで、以降Swagger UIから送信するリクエストの `Authorization` ヘッダーにトークンが自動的に付与されます

#### curl/Postmanでのトークン使用

curlを使用する場合は、Authorizationヘッダーにトークンを設定します：

```bash
curl -X GET "http://localhost:8080/api/some-protected-endpoint" \
 -H "accept: application/json" \
 -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

Postmanを使用する場合は、Authorizationタブで「Bearer Token」を選択し、トークンを入力します。

## 主要API一覧

本プロジェクトで提供されている主要なAPIエンドポイントの一覧です。詳細な使用方法はSwagger UIを参照してください。

| エンドポイント | メソッド | 認証要否 | 説明 |
|--------------|---------|---------|-----|
| `/api/authentication/login` | POST | 不要 | メールアドレスとパスワードでログインし、JWT取得 |
| `/api/authentication/signup` | POST | 不要 | 新規ユーザー登録 |
| `/api/users/profile` | GET | 必要 | ログインユーザーのプロファイル情報取得 |
| `/api/users/profile` | PUT | 必要 | ログインユーザーのプロファイル情報更新 |

## トラブルシューティング

APIの操作中に問題が発生した場合は、以下を確認してください：

1. **認証関連**:
   - トークンの有効期限切れ → 再度ログインして新しいトークンを取得
   - トークン形式の誤り → `Bearer` の後にスペースを入れてトークンを設定

2. **リクエスト形式**:
   - Content-TypeがJSON形式になっているか確認 (`application/json`)
   - リクエストボディの形式が正しいか確認

3. **サーバー稼働状況**:
   - アプリケーションが起動しているか確認
   - 設定しているポート番号が正しいか確認

APIエラーが発生した場合は、サーバーのログを確認して詳細を調査してください。

## Postman

APIテスト用のGUIツールとしてPostmanの使用もおすすめします。現在、このプロジェクト用のPostmanコレクションは提供されていませんが、必要に応じて以下の手順で作成できます：

1. Postmanをインストール (https://www.postman.com/downloads/)
2. 新規コレクションを作成
3. 各APIエンドポイントをリクエストとして追加
4. 環境変数を設定（BaseURL、トークンなど）

これにより、APIのテストや操作が効率化されます。


## GLOSSARY


このドキュメントでは、`ecsite-v2` プロジェクトに関連するビジネス用語、技術用語、略語などを定義します。新しい開発者がプロジェクト固有の用語を理解する助けとなります。

## ビジネス用語

| 用語 | 説明 |
|------|------|
| ECサイト | Electronic Commerce Site (電子商取引サイト)の略。インターネット上で商品やサービスの売買を行うWebサイト。 |

## アーキテクチャ・設計用語

| 用語 | 説明 | 本プロジェクトでの使用例 |
|------|------|----------------------|
| DDD (Domain-Driven Design) | ドメインモデルを中心に据えた設計手法。ビジネスロジックをドメイン層に集中させ、技術的な実装の詳細から分離する。 | ドメインモデル(`Account`)、リポジトリインターフェース(`Accounts`)、ドメインサービス(`AccountFactory`) |
| オニオンアーキテクチャ | 依存関係の方向を内側に向けた多層アーキテクチャ。中心にドメインモデル、その外側にアプリケーション層、さらに外側にインフラストラクチャ層を配置。 | `domain`, `application`, `infrastructure` パッケージ構造 |
| モジュラーモノリス | 単一のアプリケーションとしてデプロイされるが、内部的には明確な境界を持つモジュールに分割されたアーキテクチャ。 | `auth`, `share` モジュールの分離と明確なインターフェース |
| リアクティブプログラミング | 非同期データストリームを使用したプログラミングパラダイム。イベント駆動型でバックプレッシャーを処理できる。 | `Mono<T>`, `Flux<T>`を使用した非同期処理 |
| ドメインモデル | ビジネス領域の概念やルールを表現するオブジェクトモデル。 | `Account`, `Authentication`, `Cart`, `Order`, `Payment`, `OrderStatus`, `PaymentStatus`など |
| Value Object (VO) | 同一性ではなく属性値によって識別される不変なオブジェクト。 | `Email`, `AccountId`, `ProductId`, `CartId`, `OrderId`, `PaymentId` |
| Entity (エンティティ) | 同一性を持ち、ライフサイクルを通じて変化する可能性のあるオブジェクト。 | `Account`, `Cart`, `Order`, `Payment` (ID による同一性) |
| Factory Pattern (ファクトリーパターン) | オブジェクト生成のロジックを専用のクラスに集約する設計パターン。 | `ProductFactory`, `CategoryFactory` |
| Aggregate (集約) | 一貫性の境界を形成するエンティティと値オブジェクトのクラスター。 | `Account` と関連する `Authentication`, `Cart` と関連する `CartItem` |
| Repository (リポジトリ) | 集約の永続化と取得を抽象化するインターフェース。 | `Accounts`, `Carts`, `Orders`, `Payments` インターフェース |
| Application Service (アプリケーションサービス) | ユースケースを実装し、ドメインオブジェクトを調整するサービス。 | `LoginUsecase`, `SignupUsecase` |
| DTO (Data Transfer Object) | レイヤー間でデータを受け渡すためのオブジェクト。 | APIリクエスト/レスポンスのDTOクラス |
| ワークフロー | 一連の処理ステップを定義・実行するパターン。 | `LoginWorkflow`, `SignupWorkflow`, `AddItemToCartWorkflow`, `CreateOrderFromCartWorkflow` |
| ステップ | ワークフロー内の個別の処理単位。 | `FindAccountByEmailStep`, `VerifyWithPasswordStep` |

## プロジェクト技術スタック

| 技術 | 説明 | バージョン | 使用目的 |
|------|------|----------|---------|
| Java | オブジェクト指向プログラミング言語 | 23 | アプリケーション開発の基盤言語 |
| Spring Boot | Javaアプリケーションフレームワーク | 3.4.3 | アプリケーション構築・設定の簡素化 |
| Spring Modulith | モジュラーモノリスの開発支援ライブラリ | 1.4.0-M3 | モジュール間の依存関係管理 |
| Spring WebFlux | Springのリアクティブウェブフレームワーク | Spring Boot同梱 | リアクティブエンドポイント実装 |
| Project Reactor | リアクティブストリーム実装 | Spring Boot同梱 | 非同期・ノンブロッキング処理 (`Mono`, `Flux`) |
| MongoDB | ドキュメント指向NoSQLデータベース | 7.x | データ永続化 |
| Spring Data MongoDB Reactive | MongoDBのリアクティブドライバ | Spring Boot同梱 | MongoDB へのリアクティブアクセス |
| Spring Security | セキュリティフレームワーク | Spring Boot同梱 | 認証・認可機能の実装 |
| JWT (JSON Web Token) | 認証情報の安全な受け渡しの仕組み | Auth0 Java JWT 4.4.0 | 認証トークンの生成・検証 |
| JSpecify | Javaのnull安全性アノテーション | 1.0.0 | null参照バグの防止 |
| NullAway | コンパイル時のnull参照チェックツール | 0.12.6 | null参照バグの防止 |
| Lombok | ボイラープレート削減ツール | 1.18.38 | getter/setterなどの自動生成 |
| Spotless | コードフォーマッター | 7.0.3 | コードスタイルの統一 |
| Error Prone | 静的解析ツール | 2.37.0 | コンパイル時のバグ検出 |
| Testcontainers | テスト用コンテナ管理 | JUnit5統合版 | 結合テスト時のMongoDB環境提供 |
| Gradle | ビルドツール | Wrapper 8.8 | ビルド・テスト・依存関係管理 |
| Lefthook | Gitフック管理ツール | 1.5.5 | コミット前の自動チェック |

## モジュール構成

| モジュール | 責務 | 主要コンポーネント |
|-----------|------|------------------|
| `auth` | 認証・ユーザーアカウント管理 | `Account`, `Authentication`, `LoginWorkflow`, `SignupWorkflow` |
| `userprofile` | ユーザー情報・住所管理 | `UserProfile`, `Address`, `AddressFactory`, `CreateUserProfileWorkflow` |
| `product` | 商品・カテゴリ・在庫・プロモーション管理 | `Product`, `Category`, `Inventory`, `Promotion`, `ProductFactory`, `CategoryFactory` |
| `shopping` | カート・注文・決済管理 | `Cart`, `CartItem`, `Order`, `OrderItem`, `Payment`, `CartEvent`, `OrderEvent`, `PaymentEvent` |
| `share` | 複数モジュールで共有されるコンポーネント | `Email`, `IdGenerator`, `DomainException` |

## 重要な略語

| 略語 | 正式名称 | 説明 |
|------|---------|------|
| API | Application Programming Interface | アプリケーションの機能を外部から利用するためのインターフェース |
| CI | Continuous Integration | 継続的インテグレーション。自動ビルド・テスト実行のプロセス |
| CD | Continuous Delivery / Deployment | 継続的デリバリー/デプロイ。自動リリースプロセス |
| CSRF | Cross-Site Request Forgery | Webアプリケーションの脆弱性の一種 |
| DDD | Domain-Driven Design | ドメイン駆動設計。ビジネスドメインを中心に据えた設計手法 |
| DTO | Data Transfer Object | レイヤー間でのデータ受け渡し用オブジェクト |
| EC | Electronic Commerce | 電子商取引 |
| E2E | End-to-End | ユーザーの操作を模倣する総合テスト |
| JWT | JSON Web Token | JSONベースの認証トークン形式 |
| JVM | Java Virtual Machine | Javaプログラムを実行する仮想マシン |
| PR | Pull Request | コード変更の提案・レビュープロセス |
| RBAC | Role-Based Access Control | 役割ベースのアクセス制御 |
| VO | Value Object | 値オブジェクト。同一性ではなく内容で識別される不変オブジェクト |
| WIP | Work In Progress | 作業中・未完成の状態を示す |


## モジュール詳細


### Auth モジュール


認証・認可機能とアカウント管理を担当します。

## 責務
- ユーザー認証（ログイン）
- アカウント作成（サインアップ）
- JWT管理

## 主要コンポーネント

### ドメインモデル
- `Account`: アカウント情報を表す集約ルート
- `Authentication`: 認証方法の共通インターフェース（現在は`EmailAuthentication`のみ）
- `JsonWebToken`: JWTを表す値オブジェクト

### リポジトリ
`Accounts`: アカウントの検索・保存（`findByEmail`, `save`）

### ワークフロー
- `LoginWorkflow`: ログイン処理
- `SignupWorkflow`: アカウント作成処理

## 主要フロー

### ログイン
1. メールアドレス・パスワード受信 → アカウント検索 → パスワード検証 → JWT生成

### サインアップ
1. メールアドレス・パスワード受信 → メールアドレス重複チェック → アカウント作成 → 保存

## 認証機構
- JWTを使用（`Authorization: Bearer <token>`）
- Spring Securityによる保護
### Share モジュール


他モジュールで共通利用される基盤コンポーネントを提供します。

## 主要コンポーネント

- **`Email`**: メールアドレスの値オブジェクト
- **`AuditInfo`**: 監査情報（作成日時、更新日時）を保持するrecord
- **`IdGenerator`**: UUID生成インターフェース
- **`TimeProvider`**: 現在時刻取得のためのインターフェース
- **`DomainException`**: 基底例外クラス

## 設計方針

- 他モジュールから依存される基盤モジュール
- 逆方向の依存関係は持たない（他モジュールへの依存なし）
- 外部依存（時刻取得など）はインターフェースで抽象化し、テスト容易性を向上
# ヘルプとガイドライン
作業に必要な資料は`.roo/rules`配下にあります。基本的に `.roo/rules/`配下に前任者が作成したこのプロジェクトのメモ書きがありますので、まずはそれを確認してください。
また、`./docs/architect`配下にアーキテクチャの概要図が、`./docs/domain`にドメインモデルの詳細がありますので、必要に応じて確認してください。

信頼度は `./docs/architect`&`./docs/domain` > `src/` >`.roo/rules/` の順です。
基本的にはソースコードが正しく、ドキュメントは参考程度にしてください。
但し、`./docs/architect`&`./docs/domain`は概要設計者が作成したドキュメントを元に作成されていますので、信頼度は高いです。

作業の流れは以下の通りです。
1. 作業に必要なドキュメントを確認し、作業内容を理解する
2. 作業に必要な情報を収集する。この際、必要な情報はマネージャーに確認したり、githubのissueを確認したり、過去のコミットを確認したりしてください。
github は github mcp 経由でアクセスすることが可能です。また、brave mcpを使用することでブラウジングが可能です。
3. 作業を行う。いちいちマネージャーに確認する必要はありませんが、作業内容を理解してから作業を行ってください。
4. 作業が完了したら、作業内容に応じて、ドキュメントを更新してください。
5. 最後にgit commitを行ってください。コミットメッセージのルールは `.roo/rules/08_WORKFLOW.md` で確認してください。

