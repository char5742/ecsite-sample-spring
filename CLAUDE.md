# CLAUDE.md

このファイルは、このリポジトリでコードを扱う際にClaude Code (claude.ai/code)に指針を提供します。

## ビルドコマンド

- ビルド: `./gradlew build`
- アプリケーション実行: `./gradlew bootRun`
- 全テスト実行: `./gradlew test`
- 単一テスト実行: `./gradlew test --tests "com.example.ec_2024b_back.auth.domain.models.AuthenticationTest"`
- タグ指定テスト実行: `./gradlew test -PincludeTags="Fast"` または `-PexcludeTags="Slow"`
- フォーマットチェック: `./gradlew spotlessCheck`
- フォーマット適用: `./gradlew spotlessApply`
- ビルド成果物クリーンアップ: `./gradlew clean`

### Dockerコマンド

- MongoDB起動: `docker run --name local-mongo -p 27017:27017 -v ecsite-mongo-data:/data/db -d mongo:latest`
- Docker Composeで実行: `docker compose up -d`
- APIテスト実行: `docker compose run --rm api-tests`

## アーキテクチャ概要

このプロジェクトは**オニオンアーキテクチャ**と**ドメイン駆動設計(DDD)**の原則に従い、**Spring Modulith**を使用したモジュラーモノリス設計を採用しています。

### レイヤー構造

1. **ドメイン層（中心）**
   - ビジネスロジック、モデル、リポジトリインターフェースを含む
   - フレームワーク依存のない純粋なJava
   - 値オブジェクト: `Email`, `AccountId`, `ProductId`（不変のrecord）
   - エンティティ: `Account`, `Cart`, `Order`, `Payment`
   - リポジトリインターフェース: `Accounts`, `Carts`, `Orders`

2. **アプリケーション層**
   - ユースケースを実装し、ドメイン操作を調整
   - ワークフロー（複数ステップのプロセス）とステップ（単一操作）を含む
   - 例: `LoginWorkflow`, `CreateOrderFromCartWorkflow`

3. **インフラストラクチャ層**
   - 技術的な実装: APIハンドラー、リポジトリ実装
   - MongoDBドキュメントとリアクティブリポジトリ
   - Spring Security設定とJWT実装

### モジュール構成

- `auth` - 認証・認可
- `userprofile` - ユーザープロファイルと住所管理
- `product` - 商品、カテゴリ、在庫、プロモーション管理
- `shopping` - ショッピングカート、注文、決済管理
- `logistics` - 配送追跡
- `share` - 複数モジュールで共有されるコンポーネント

### 主要パターン

- **ファクトリーパターン**: 専用ファクトリーによるオブジェクト生成（`AccountFactory`, `ProductFactory`）
- **ワークフロー/ステップパターン**: ビジネスプロセスを再利用可能なステップに分割
- **リアクティブプログラミング**: 全体を通じて`Mono`/`Flux`による非ブロッキングI/O
- **値オブジェクト**: recordとしての不変な識別子
- **リポジトリパターン**: ドメイン層にインターフェース、インフラストラクチャ層に実装

## 開発ガイドライン

### コードスタイル
- SpotlessによるGoogle Java Formatの自動適用
- Lefthookのpre-commitフックでフォーマット実行
- パッケージ名: 小文字のsnake_case
- テスト名: `should[期待される結果]_when[条件]`

### Null安全性
- JSpecifyアノテーション（`@Nullable`, `@NullMarked`）
- NullAway静的解析を有効化
- `LocalDateTime.now()`の直接使用を避け、`TimeProvider`インターフェースを使用

### テスト
- 単体テスト: 依存関係をモック化、`@Fast`タグを使用
- 統合テスト: MongoDBにTestcontainersを使用、`@IntegrationTest`タグを使用
- APIテスト: `test/api-tests/`のRunn YAMLベースのシナリオ
- リアクティブテスト: `Mono`/`Flux`のアサーションに`StepVerifier`を使用

### セキュリティ
- JWTベースの認証
- リアクティブ設定でのSpring Security
- BCryptによるパスワードエンコーディング