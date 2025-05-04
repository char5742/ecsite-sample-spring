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
```

- **[auth](./05_MODULES/auth/README.md)**: 認証・アカウント管理
- **[userprofile](./05_MODULES/userprofile/README.md)**: ユーザー情報・住所管理
- **[product](./05_MODULES/product/README.md)**: 商品・カテゴリ・在庫・プロモーション管理
- **[shopping](./05_MODULES/shopping/README.md)**: カート・注文・決済管理
- **[share](./05_MODULES/share/README.md)**: 共通ユーティリティ（基盤モジュール）

## 技術スタック

- **リアクティブ**: Spring WebFlux + Project Reactor（`Mono`/`Flux`）による非同期処理
- **セキュリティ**: JWTベース認証、`PasswordEncoder`によるパスワードハッシュ化