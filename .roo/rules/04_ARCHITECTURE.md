# アーキテクチャ概要

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
- **Config層**: アプリケーション全体の設定（セキュリティ、Web設定、ルーティング）

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
    sample --> share;
```

### 各モジュールの責務

- **[auth](./05_MODULES/auth/README.md)**: 認証・アカウント管理
  - allowedDependencies: `share`
- **[userprofile](./05_MODULES/userprofile/README.md)**: ユーザー情報・住所管理
  - allowedDependencies: `share`, `auth`
  - type: CLOSED
- **[product](./05_MODULES/product/README.md)**: 商品・カテゴリ・在庫・プロモーション管理
  - デフォルト設定（OPEN）
- **[shopping](./05_MODULES/shopping/README.md)**: カート・注文・決済管理
  - allowedDependencies: `share`, `auth`, `product`
  - type: CLOSED
- **[logistics](./05_MODULES/logistics/README.md)**: 配送管理・配送状態追跡
  - allowedDependencies: `share`, `shopping`
- **[share](./05_MODULES/share/README.md)**: 共通ユーティリティ（基盤モジュール）
  - 他のモジュールに依存しない
- **[sample](./05_MODULES/sample/README.md)**: 新規モジュール作成の参考となるサンプルモジュール
  - allowedDependencies: `share`

## 設定クラス（configパッケージ）

- **SecurityConfig**: パスワードエンコーダーなどのセキュリティ関連Bean定義
- **WebConfig**: エラーハンドリング、CORS設定などのWeb関連設定
- **WebSecurityConfig**: Spring Security WebFluxのセキュリティフィルターチェーン設定
- **RouterConfig**: WebFluxの関数型ルーティング設定（ルートレベルに配置）

## 技術スタック

- **リアクティブ**: Spring WebFlux + Project Reactor（`Mono`/`Flux`）による非同期処理
- **セキュリティ**: JWTベース認証、`PasswordEncoder`によるパスワードハッシュ化