# ecsite-sample-spring 開発ガイド

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
- **[10_TROUBLESHOOTING.md](./10_TROUBLESHOOTING.md)** - トラブルシューティング
- **[11_API_USAGE.md](./11_API_USAGE.md)** - API利用方法
- **[12_GLOSSARY.md](./12_GLOSSARY.md)** - 用語集

## 設計原則

- **技術分離**: ドメイン層とインフラ層の厳格な分離
- **リポジトリパターン**: インターフェースはドメイン層、実装はインフラ層
- **不変値オブジェクト**: `Email`, `AccountId`などの値オブジェクトは不変設計
- **ファクトリーパターン**: オブジェクト生成の責務を専用のファクトリークラスに集約
- **モジュール構成**: 
  - **auth**: 認証・認可
  - **share**: 共通コンポーネント
  - **userprofile**: ユーザー情報・住所管理
  - **product**: 商品・カテゴリ・在庫・プロモーション管理
  - **shopping**: ショッピングカート・注文・決済管理
  - **logistics**: 配送管理・配送状態追跡
  - **sample**: 新規モジュール作成の参考実装

## 開発指針

`.roo/rules/instruction.md`にAIアシスタント向けの基本指示を記載。作業後はドキュメント更新を忘れずに。